package com.ranicorp.letswatchfootballtogether.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val userUid = userPreferenceRepository.getUserUid()
    val userNickName = userPreferenceRepository.getUserNickName()
    private val isLoaded: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _profileImage: MutableStateFlow<String> = MutableStateFlow("")
    val profileImage: StateFlow<String> = _profileImage
    private val _eventsList: MutableStateFlow<List<Post>> = MutableStateFlow(emptyList())
    val eventsList: StateFlow<List<Post>> = _eventsList
    private val _eventsUidList: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val eventsUidList: StateFlow<List<String>> = _eventsUidList
    private val isDeleted: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _guideMsgResId = MutableStateFlow<Int?>(null)
    val guideMsgResId: StateFlow<Int?> = _guideMsgResId

    fun getUserInfo() {
        viewModelScope.launch {
            userRepository.getUserNoFirebaseUid(
                onComplete = { },
                onError = {
                    isLoaded.value = false
                    _guideMsgResId.value = R.string.error_message_profile_loading_failed
                },
                userUid
            ).collect { response ->
                val user = response.values.first()
                val imageRef = FirebaseStorage.getInstance().reference.child(user.profileUri)
                imageRef.downloadUrl.addOnSuccessListener {
                    _profileImage.value = it.toString()
                }
                _eventsUidList.value = user.participatingEvent
                if (user.participatingEvent.isNotEmpty()) {
                    getEvents(eventsUidList.value)
                } else {
                    _eventsList.value = emptyList()
                }
            }
        }
    }

    fun getEvents(eventsUidList: List<String>) {
        viewModelScope.launch {
            val result = mutableListOf<Post>()
            eventsUidList.forEach { eventUid ->
                postRepository.getPostNoFirebaseUid(
                    onComplete = { isLoaded.value = true },
                    onError = {
                        isLoaded.value = false
                        _guideMsgResId.value = R.string.error_message_profile_loading_failed
                    },
                    eventUid
                ).collect { response ->
                    if (response.isNotEmpty()) {
                        val postInfo = response.values.first()
                        result.add(postInfo)
                    }
                }
            }
            _eventsList.value = result
        }
    }

    fun deleteAccount() {
        if (eventsUidList.value.isEmpty()) {
            deleteUserFirebaseAuth()
            return
        }
        viewModelScope.launch {
            eventsUidList.value.forEach { postUid ->
                postRepository.getPostNoFirebaseUid(
                    onComplete = { },
                    onError = {
                        isDeleted.value = false
                        _guideMsgResId.value = R.string.error_message_delete_user_failed
                    },
                    postUid
                ).collect { response ->
                    val eventFirebaseUid = response.keys.first()
                    val post = response.values.first()
                    post.participantsUidList?.remove(userUid)
                    viewModelScope.launch {
                        postRepository.updatePost(
                            onComplete = { },
                            onError = {
                                isDeleted.value = false
                                _guideMsgResId.value = R.string.error_message_delete_user_failed
                            },
                            postUid, eventFirebaseUid, post
                        ).collect {
                        }
                    }
                }
            }
            deleteUserFirebaseAuth()
        }
    }

    private fun deleteUserFirebaseAuth() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.delete()
            ?.addOnCompleteListener {
                deleteUser()
            }
            ?.addOnFailureListener {
                isDeleted.value = false
            }
    }

    private fun deleteUser() {
        viewModelScope.launch {
            userRepository.deleteUserNoFirebaseUid(
                onComplete = {
                    isDeleted.value = true
                    _guideMsgResId.value = R.string.guide_message_delete_user_successful
                    userPreferenceRepository.clearPreferences()
                },
                onError = {
                    isDeleted.value = false
                    _guideMsgResId.value = R.string.error_message_delete_user_failed
                },
                userUid
            ).collect { }
        }
    }
}