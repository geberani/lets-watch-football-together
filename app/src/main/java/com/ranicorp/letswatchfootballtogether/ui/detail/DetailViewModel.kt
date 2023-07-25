package com.ranicorp.letswatchfootballtogether.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
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
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _selectedPost = MutableLiveData<Post>()
    val selectedPost: LiveData<Post> = _selectedPost
    private val participantsUidList = mutableListOf<String>()
    private val _participantsInfo = MutableLiveData<Event<MutableList<User>>>()
    val participantsInfo: LiveData<Event<MutableList<User>>> = _participantsInfo
    private val _isLoaded = MutableLiveData<Event<Boolean>>()
    val isLoaded: LiveData<Event<Boolean>> = _isLoaded
    private val _isParticipated = MutableLiveData<Boolean>()
    val isParticipated: LiveData<Boolean> = _isParticipated
    private val _isParticipateCompleted = MutableLiveData<Event<Boolean>>()
    val isParticipateCompleted: LiveData<Event<Boolean>> = _isParticipateCompleted
    private val userUid = userPreferenceRepository.getUserUid()
    private var postFirebaseUid = ""
    private var userInfo: Map<String, User>? = emptyMap()

    fun getPostDetail(postUid: String) {
        viewModelScope.launch {
            val postResponse = postRepository.getPostNoFirebaseUid(postUid)
            when (postResponse) {
                is ApiResultSuccess -> {
                    postFirebaseUid = postResponse.data?.keys?.first() ?: ""
                    _selectedPost.value = postResponse.data?.values?.first() ?: Post()
                    participantsUidList.addAll(
                        selectedPost.value?.participantsUidList ?: emptyList()
                    )
                    getParticipantsDetail()
                    isUserParticipated()
                }
                is ApiResultError -> {
                    _isLoaded.value = Event(false)
                    Log.d(
                        "DetailViewModel",
                        "Error code: ${postResponse.code}, message: ${postResponse.message}"
                    )
                }
                is ApiResultException -> {
                    _isLoaded.value = Event(false)
                    Log.d("DetailViewModel", "Exception: ${postResponse.throwable}")
                }
            }
        }
    }

    private fun getParticipantsDetail() {
        viewModelScope.launch {
            val participantsUidList = selectedPost.value?.participantsUidList
            val participantsList = mutableListOf<User>()
            participantsUidList?.forEach { participantUid ->
                val userInfoCall = userRepository.getUserNoFirebaseUid(participantUid)
                when (userInfoCall) {
                    is ApiResultSuccess -> {
                        participantsList.add(
                            userInfoCall.data?.values?.first() ?: User(
                                "",
                                "",
                                "",
                                mutableListOf()
                            )
                        )
                    }
                    is ApiResultError -> {
                        _isLoaded.value = Event(false)
                        Log.d(
                            "DetailViewModel",
                            "Error code: ${userInfoCall.code}, message: ${userInfoCall.message}"
                        )
                    }
                    is ApiResultException -> {
                        _isLoaded.value = Event(false)
                        Log.d("DetailViewModel", "Exception: ${userInfoCall.throwable}")
                    }
                }
            }
            _participantsInfo.value =
                Event(mutableListOf<User>().apply { addAll(participantsList) })
        }
    }


    private fun isUserParticipated() {
        _isParticipated.value =
           participantsUidList.contains(userUid)
    }

    fun participate() {
        viewModelScope.launch {
            val userResponse = userRepository.getUserNoFirebaseUid(userUid)
            when (userResponse) {
                is ApiResultSuccess -> {
                    userInfo = userResponse.data
                    updatePost()
                }
                is ApiResultError -> {
                    _isParticipateCompleted.value = Event(false)
                    Log.d(
                        "DetailViewModel",
                        "Error code: ${userResponse.code}, message: ${userResponse.message}"
                    )
                }
                is ApiResultException -> {
                    _isParticipateCompleted.value = Event(false)
                    Log.d("DetailViewModel", "Exception: ${userResponse.throwable}")
                }
            }
        }
    }

    private fun updatePost() {
        viewModelScope.launch {
            val post = _selectedPost.value
            post?.participantsUidList?.add(userUid)
            val updatePostResponse = postRepository.updatePost(
                post?.postUid ?: "",
                postFirebaseUid,
                post ?: Post()
            )
            when (updatePostResponse) {
                is ApiResultSuccess -> {
                    updateUser(selectedPost.value?.postUid ?: "")
                }
                is ApiResultError -> {
                    _isParticipateCompleted.value = Event(false)
                    Log.d(
                        "DetailViewModel",
                        "Error code: ${updatePostResponse.code}, message: ${updatePostResponse.message}"
                    )
                }
                is ApiResultException -> {
                    _isParticipateCompleted.value = Event(false)
                    Log.d("DetailViewModel", "Exception: ${updatePostResponse.throwable}")
                }
            }
        }
    }

    private fun updateUser(postUid: String) {
        viewModelScope.launch {
            val userFirebaseUid = userInfo?.keys?.first() ?: ""
            val user = userInfo?.values?.first()
            user?.participatingEvent?.add(postUid)
            val updateUserCall = userRepository.updateUser(userUid, userFirebaseUid, user ?: TODO())
            when (updateUserCall) {
                is ApiResultSuccess -> {
                    getPostDetail(postUid)
                    _isParticipated.value = true
                    _isParticipateCompleted.value = Event(true)
                }
                is ApiResultError -> {
                    _isParticipateCompleted.value = Event(false)
                    Log.d(
                        "DetailViewModel",
                        "Error code: ${updateUserCall.code}, message: ${updateUserCall.message}"
                    )
                }
                is ApiResultException -> {
                    _isParticipateCompleted.value = Event(false)
                    Log.d("DetailViewModel", "Exception: ${updateUserCall.throwable}")
                }
            }
        }
    }
}