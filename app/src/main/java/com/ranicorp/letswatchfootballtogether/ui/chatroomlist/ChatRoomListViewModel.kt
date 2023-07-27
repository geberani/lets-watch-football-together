package com.ranicorp.letswatchfootballtogether.ui.chatroomlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.LatestChatRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomListViewModel @Inject constructor(
    private val latestChatRepository: LatestChatRepository,
    private val preferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val userUid = preferenceRepository.getUserUid()
    private val _isLoaded = MutableLiveData<Event<Boolean>>()
    val isLoaded: LiveData<Event<Boolean>> = _isLoaded
    private val _latestChatList = MutableLiveData<MutableList<ChatRoomInfo>>()
    val latestChatList: LiveData<MutableList<ChatRoomInfo>> = _latestChatList

    fun getParticipatingEventList() {
        viewModelScope.launch {
            val getUserDetailCall = userRepository.getUserNoFirebaseUid(userUid)
            when (getUserDetailCall) {
                is ApiResultSuccess -> {
                    val participatingEvents =
                        getUserDetailCall.data?.values?.first()?.participatingEvent?.toList()
                            ?: emptyList()
                    if (participatingEvents.isNotEmpty()) {
                        getLatestChat(participatingEvents)
                    } else {
                        _latestChatList.value = mutableListOf()
                    }
                }
                is ApiResultError -> {
                    _isLoaded.value = Event(false)
                    Log.d(
                        "ChatRoomListViewModel",
                        "Error code: ${getUserDetailCall.code}, message: ${getUserDetailCall.message}"
                    )
                }
                is ApiResultException -> {
                    _isLoaded.value = Event(false)
                    Log.d("ChatRoomListViewModel", "Exception: ${getUserDetailCall.throwable}")
                }
            }
        }
    }

    private fun getLatestChat(participatingEvents: List<String>) {
        viewModelScope.launch {
            if (_latestChatList.value?.isEmpty() == false) {
                _latestChatList.value?.clear()
            }

            val chatRoomInfoDeferredList = participatingEvents.map { participatingEventUid ->
                async {
                    val getLatestChatCall =
                        latestChatRepository.getLatestChat(participatingEventUid)
                    when (getLatestChatCall) {
                        is ApiResultSuccess -> {
                            if (getLatestChatCall.data !== null) {
                                getLatestChatCall.data
                            } else {
                                val postDetailCall =
                                    postRepository.getPostNoFirebaseUid(participatingEventUid)
                                when (postDetailCall) {
                                    is ApiResultSuccess -> {
                                        val post = postDetailCall.data?.values?.first()
                                        ChatRoomInfo(
                                            participatingEventUid,
                                            post?.title ?: "",
                                            "",
                                            0,
                                            post?.imageLocations?.first() ?: ""
                                        )
                                        _isLoaded.value = Event(true)
                                    }
                                    is ApiResultError -> {
                                        _isLoaded.value = Event(false)
                                        Log.d(
                                            "ChatRoomListViewModel",
                                            "Error code: ${postDetailCall.code}, message: ${postDetailCall.message}"
                                        )
                                    }
                                    is ApiResultException -> {
                                        _isLoaded.value = Event(false)
                                        Log.d(
                                            "ChatRoomListViewModel",
                                            "Exception: ${postDetailCall.throwable}"
                                        )
                                    }
                                }
                            }
                        }
                        is ApiResultError -> {
                            _isLoaded.value = Event(false)
                            Log.d(
                                "ChatRoomListViewModel",
                                "Error code: ${getLatestChatCall.code}, message: ${getLatestChatCall.message}"
                            )
                        }
                        is ApiResultException -> {
                            _isLoaded.value = Event(false)
                            Log.d(
                                "ChatRoomListViewModel",
                                "Exception: ${getLatestChatCall.throwable}"
                            )
                        }
                    }
                }
            }
            val chatRoomInfoList =
                chatRoomInfoDeferredList.awaitAll().filterIsInstance<ChatRoomInfo>()
            _latestChatList.value?.addAll(chatRoomInfoList)
        }
    }
}