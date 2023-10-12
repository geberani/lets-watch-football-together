package com.ranicorp.letswatchfootballtogether.ui.chatroomlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoom
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.source.repository.LatestChatRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
    private val _isLoaded: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isLoaded: StateFlow<Boolean?> = _isLoaded
    private val _latestChatList = MutableStateFlow<MutableList<ChatRoomInfo>>(mutableListOf())
    val latestChatList: StateFlow<MutableList<ChatRoomInfo>> = _latestChatList
    private val updatedList: MutableList<ChatRoomInfo> = mutableListOf()

    fun getParticipatingEventList() {
        viewModelScope.launch {
            userRepository.getUserNoFirebaseUid(
                onComplete = { },
                onError = {
                    _isLoaded.value = false
                    getLocalDBChatRoomList()
                },
                userUid
            ).collect { response ->
                val participatingEvents =
                    response.values.first().participatingEvent.toList()
                if (participatingEvents.isEmpty()) {
                    _isLoaded.value = true
                    _latestChatList.value = mutableListOf()
                } else {
                    getLatestChat(participatingEvents)
                }
            }
        }
    }

    private fun getLocalDBChatRoomList() {
        viewModelScope.launch {
            val savedChatRoomList = latestChatRepository.getAllLocalChatRoom()
            _isLoaded.value = true
            if (savedChatRoomList.isNullOrEmpty()) {
                _latestChatList.value = mutableListOf()
                return@launch
            }
            val result = mutableListOf<ChatRoomInfo>()
            savedChatRoomList.sortedByDescending { it.lastMsg }.forEach { chatRoom ->
                val chatRoomInfo = ChatRoomInfo(
                    chatRoom.uid,
                    chatRoom.title,
                    chatRoom.lastMsg,
                    chatRoom.lastSentTime,
                    chatRoom.imageLocation
                )
                result.add(chatRoomInfo)
            }
            _latestChatList.value = result
        }
    }

    private fun getLatestChat(participatingEvents: List<String>) {
        if (_latestChatList.value.isNotEmpty()) {
            _latestChatList.value.clear()
        }
        viewModelScope.launch {
            participatingEvents.forEach { participatingEventUid ->
                latestChatRepository.getLatestChat(
                    onComplete = { },
                    onError = { _isLoaded.value = false },
                    participatingEventUid
                ).collect { response ->
                    if (!response?.lastMsg.isNullOrEmpty()) {
                        updatedList.addAll(listOf(response!!))
                    } else {
                        getPostDetail(participatingEventUid)
                    }
                }
            }
            _latestChatList.value = updatedList
            _isLoaded.value = true
            _isLoaded.value = null
            updateRoom(_latestChatList.value.toList())
        }
    }

    private suspend fun getPostDetail(participatingEventUid: String) {
        postRepository.getPostNoFirebaseUid(
            onComplete = {  },
            onError = { _isLoaded.value = false },
            participatingEventUid
        ).collect { response ->
            val post = response.values.first()
            val newChatRoomInfo = ChatRoomInfo(
                participatingEventUid,
                post.title,
                "",
                0,
                post.imageLocations.first()
            )
            updatedList.add(newChatRoomInfo)
        }
    }

    private fun updateRoom(chatRoomInfoList: List<ChatRoomInfo>) {
        viewModelScope.launch {
            latestChatRepository.deleteAllChatRooms()
            chatRoomInfoList.forEach { chatRoomInfo ->
                val chatRoom = ChatRoom(
                    chatRoomInfo.uid,
                    chatRoomInfo.title,
                    chatRoomInfo.lastMsg,
                    chatRoomInfo.lastSentTime,
                    chatRoomInfo.imageLocation
                )
                latestChatRepository.insertLocal(chatRoom)
            }
        }
    }
}