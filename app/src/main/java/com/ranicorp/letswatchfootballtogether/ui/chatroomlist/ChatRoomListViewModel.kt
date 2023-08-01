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
                if (participatingEvents.isNotEmpty()) {
                    getLatestChat(participatingEvents)
                } else {
                    _isLoaded.value = true
                    _latestChatList.value = mutableListOf()
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
                    if (response != null) {
                        val currentList = _latestChatList.value
                        currentList.add(response)
                        _latestChatList.value = currentList
                    } else {
                        getPostDetail(participatingEventUid)
                    }
                }
            }
            _isLoaded.value = true
            _isLoaded.value = null
            updateRoom(_latestChatList.value.toList())
        }
    }

    private fun getPostDetail(participatingEventUid: String) {
        viewModelScope.launch {
            postRepository.getPostNoFirebaseUid(
                onComplete = { _isLoaded.value = true },
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
                val currentList = _latestChatList.value
                currentList.add(newChatRoomInfo)
                _latestChatList.value = currentList
            }
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