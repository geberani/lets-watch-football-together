package com.ranicorp.letswatchfootballtogether.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val _isLoaded: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isLoaded: LiveData<Event<Boolean>> = _isLoaded
    private val _profileImage: MutableLiveData<Event<String>> = MutableLiveData()
    val profileImage: LiveData<Event<String>> = _profileImage
    private val _eventsList: MutableLiveData<Event<List<Post>>> = MutableLiveData()
    val eventsList: LiveData<Event<List<Post>>> = _eventsList
    private val _eventsUidList: MutableLiveData<Event<List<String>>> = MutableLiveData()
    val eventsUidList: LiveData<Event<List<String>>> = _eventsUidList
    private val _isDeleted: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isDeleted: LiveData<Event<Boolean>> = _isDeleted

    fun getUserInfo() {
        viewModelScope.launch {
            val networkResult = userRepository.getUserNoFirebaseUid(userUid)
            when (networkResult) {
                is ApiResultSuccess -> {
                    val user = networkResult.data?.values?.first()
                    _profileImage.value = Event(user?.profileUri ?: "")
                    _eventsUidList.value = Event(user?.participatingEvent ?: mutableListOf())
                    getEvents(eventsUidList.value?.content ?: emptyList())
                }
                is ApiResultError -> {
                    _isLoaded.value = Event(false)
                    Log.d(
                        "ProfileViewModel",
                        "Error code: ${networkResult.code}, message: ${networkResult.message}"
                    )
                }
                is ApiResultException -> {
                    _isLoaded.value = Event(false)
                    Log.d("ProfileViewModel", "Exception: ${networkResult.throwable}")
                }
            }
        }
    }

    fun getEvents(eventsUidList: List<String>) {
        viewModelScope.launch {
            val result = mutableListOf<Post>()
            eventsUidList.forEach { eventUid ->
                val networkResult = postRepository.getPostNoFirebaseUid(eventUid)
                when (networkResult) {
                    is ApiResultSuccess -> {
                        val postInfo = networkResult.data?.values?.first() ?: Post()
                        result.add(postInfo)
                        _isLoaded.value = Event(true)
                    }
                    is ApiResultError -> {
                        _isLoaded.value = Event(false)
                        Log.d(
                            "ProfileViewModel",
                            "Error code: ${networkResult.code}, message: ${networkResult.message}"
                        )
                    }
                    is ApiResultException -> {
                        _isLoaded.value = Event(false)
                        Log.d("ProfileViewModel", "Exception: ${networkResult.throwable}")
                    }
                }
            }
            _eventsList.value = Event(result)
        }
    }

    fun deleteAccount() {
        if (eventsUidList.value?.content.isNullOrEmpty()) {
            deleteUser()
            return
        }
        viewModelScope.launch {
            eventsUidList.value?.content?.forEach { postUid ->
                val networkResult = postRepository.getPostNoFirebaseUid(postUid)
                when (networkResult) {
                    is ApiResultSuccess -> {
                        val eventFirebaseUid = networkResult.data?.keys?.first() ?: ""
                        val post = networkResult.data?.values?.first()
                        post?.participantsUidList?.remove(userUid)
                        val updatePostCall =
                            postRepository.updatePost(postUid, eventFirebaseUid, post ?: Post())
                        when (updatePostCall) {
                            is ApiResultSuccess -> {

                            }
                            is ApiResultError -> {
                                _isDeleted.value = Event(false)
                                Log.d(
                                    "ProfileViewModel",
                                    "Error code: ${updatePostCall.code}, message: ${updatePostCall.message}"
                                )
                            }
                            is ApiResultException -> {
                                _isDeleted.value = Event(false)
                                Log.d("ProfileViewModel", "Exception: ${updatePostCall.throwable}")
                            }
                        }
                    }
                    is ApiResultError -> {
                        _isDeleted.value = Event(false)
                        Log.d(
                            "ProfileViewModel",
                            "Error code: ${networkResult.code}, message: ${networkResult.message}"
                        )
                    }
                    is ApiResultException -> {
                        _isDeleted.value = Event(false)
                        Log.d("ProfileViewModel", "Exception: ${networkResult.throwable}")
                    }
                }
            }
            deleteUser()
        }
    }

    fun deleteUser() {
        viewModelScope.launch {
            val networkResult = userRepository.deleteUserNoFirebaseUid(userUid)
            when (networkResult) {
                is ApiResultSuccess -> {
                    userPreferenceRepository.clearPreferences()
                    _isDeleted.value = Event(true)
                }
                is ApiResultError -> {
                    _isDeleted.value = Event(false)
                    Log.d(
                        "ProfileViewModel",
                        "Error code: ${networkResult.code}, message: ${networkResult.message}"
                    )
                }
                is ApiResultException -> {
                    _isDeleted.value = Event(false)
                    Log.d("ProfileViewModel", "Exception: ${networkResult.throwable}")
                }
            }
        }
    }
}