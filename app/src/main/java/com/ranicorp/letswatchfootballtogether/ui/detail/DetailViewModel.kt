package com.ranicorp.letswatchfootballtogether.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {
    private val _selectedPost: MutableStateFlow<Post> = MutableStateFlow(Post())
    val selectedPost: StateFlow<Post> = _selectedPost
    private val participantsUidList = mutableListOf<String>()
    private val _participantsInfo: MutableStateFlow<MutableList<User>> = MutableStateFlow(
        mutableListOf()
    )
    val participantsInfo: StateFlow<MutableList<User>> = _participantsInfo
    private val _isLoaded: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isLoaded: StateFlow<Boolean?> = _isLoaded
    private val _isParticipated: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isParticipated: StateFlow<Boolean> = _isParticipated
    private val _isParticipateCompleted: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isParticipateCompleted: StateFlow<Boolean?> = _isParticipateCompleted
    private val userUid = userPreferenceRepository.getUserUid()
    private var postFirebaseUid = ""
    private var userInfo: Map<String, User> = emptyMap()

    fun getPostDetail(postUid: String) {
        viewModelScope.launch {
            postRepository.getPostNoFirebaseUid(
                onComplete = { getParticipantsDetail() },
                onError = { _isLoaded.value = false },
                postUid
            ).collect { response ->
                postFirebaseUid = response.keys.first()
                _selectedPost.value = response.values.first()
                if (participantsUidList.isNotEmpty()) {
                    participantsUidList.clear()
                }
                participantsUidList.addAll(selectedPost.value.participantsUidList ?: emptyList())
            }
        }
    }

    private fun getParticipantsDetail() {
        viewModelScope.launch {
            val participantsUidList = selectedPost.value.participantsUidList
            val participantsList = mutableListOf<User>()
            participantsUidList?.forEach { participantUid ->
                userRepository.getUserNoFirebaseUid(
                    onComplete = { },
                    onError = { _isLoaded.value = false },
                    participantUid
                ).collect { response ->
                    participantsList.add(response.values.first())
                }
            }
            _participantsInfo.value = mutableListOf<User>().apply { addAll(participantsList) }
            isUserParticipated()
        }
    }

    private fun isUserParticipated() {
        _isParticipated.value = participantsUidList.contains(userUid)
    }

    fun participate() {
        viewModelScope.launch {
            userRepository.getUserNoFirebaseUid(
                onComplete = { updatePost() },
                onError = { _isParticipateCompleted.value = false },
                userUid
            ).collect { response ->
                userInfo = response
            }
        }
    }

    private fun updatePost() {
        viewModelScope.launch {
            val post = _selectedPost.value
            post.participantsUidList?.add(userUid)
            postRepository.updatePost(
                onComplete = { updateUser(selectedPost.value.postUid) },
                onError = { _isParticipateCompleted.value = false },
                post.postUid,
                postFirebaseUid,
                post
            ).collect { }
        }
    }

    private fun updateUser(postUid: String) {
        viewModelScope.launch {
            val userFirebaseUid = userInfo.keys.first()
            val user = userInfo.values.first()
            user.participatingEvent.add(postUid)
            userRepository.updateUser(
                onComplete = {
                    getPostDetail(postUid)
                    _isParticipated.value = true
                    _isParticipateCompleted.value = true
                },
                onError = { _isParticipateCompleted.value = false },
                userUid, userFirebaseUid, user
            ).collect { }
        }
    }
}