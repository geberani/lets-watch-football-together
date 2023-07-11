package com.ranicorp.letswatchfootballtogether.ui.chatroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.repository.ChatRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val preferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _allChat = MutableLiveData<Event<List<Message>>>()
    val allChat: LiveData<Event<List<Message>>> = _allChat
    private var _postUid: String = ""
    val userUid = preferenceRepository.getUserUid()
    private val _isSent = MutableLiveData<Event<Boolean>>()
    val isSent: LiveData<Event<Boolean>> = _isSent
    private var chatEventListener: ChildEventListener? = null

    fun addChat(messageText: String) {
        viewModelScope.launch {
            val message = Message(
                userUid,
                preferenceRepository.getUserNickName(),
                userRepository.getUserInfo(userUid)?.profileUri ?: "",
                System.currentTimeMillis(),
                messageText
            )
            if (chatRepository.addChat(_postUid, message).isSuccessful) {
                _isSent.value = Event(true)
            }
        }
    }

    fun getAllChat() {
        viewModelScope.launch {
            _allChat.value =
                Event(
                    chatRepository.getAllChat(_postUid).body()?.values?.toList()
                        ?: emptyList()
                )
        }
    }

    fun setPostUid(postUid: String) {
        _postUid = postUid
    }

    fun addChatEventListener() {
        chatEventListener = chatRepository.addChatEventListener(_postUid) { chatItem ->
            val currentList = _allChat.value?.content ?: emptyList()
            val newList = currentList.toMutableList().apply { add(chatItem) }
            _allChat.value = Event(newList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatEventListener = null
    }
}