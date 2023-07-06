package com.ranicorp.letswatchfootballtogether.ui.chatroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.repository.ChatRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val preferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _allChat = MutableLiveData<List<Message>>()
    val allChat: LiveData<List<Message>> = _allChat
    private var _postUid: String = ""
    val postUid = _postUid
    val userUid = preferenceRepository.getUserUid()

    fun addChat(messageText: String) {
        viewModelScope.launch {
            val message = Message(
                userUid,
                preferenceRepository.getUserNickName(),
                userRepository.getUserInfo(userUid)?.profileUri ?: TODO(),
                System.currentTimeMillis(),
                messageText
            )
            chatRepository.addChat(postUid, message)
        }
    }

    fun getAllChat() {
        viewModelScope.launch {
            _allChat.value =
                chatRepository.getAllChat(postUid).body()?.values?.toList()
                    ?: emptyList()
        }
    }

    fun setPostUid(postUid: String) {
        _postUid = postUid
    }
}