package com.ranicorp.letswatchfootballtogether.ui.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
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
    private val _isSendingComplete = MutableLiveData<Event<Boolean>>()
    val isSendingComplete: LiveData<Event<Boolean>> = _isSendingComplete
    private val _isLoaded = MutableLiveData<Event<Boolean>>()
    val isLoaded: LiveData<Event<Boolean>> = _isLoaded
    private var chatEventListener: ChildEventListener? = null

    fun sendChat(messageText: String) {
        viewModelScope.launch {
            var profileUri = ""
            if (profileUri.isEmpty()) {
                val userInfoCall = userRepository.getUserNoFirebaseUid(userUid)
                when (userInfoCall) {
                    is ApiResultSuccess -> {
                        val user = userInfoCall.data.values.first()
                        profileUri = user.profileUri
                    }
                    is ApiResultError -> {
                        _isSendingComplete.value = Event(false)
                        Log.d(
                            "ChatRoomViewModel",
                            "Error code: ${userInfoCall.code}, message: ${userInfoCall.message}"
                        )
                    }
                    is ApiResultException -> {
                        _isSendingComplete.value = Event(false)
                        Log.d("ChatRoomViewModel", "Exception: ${userInfoCall.throwable}")
                    }
                }
            }

            val message = Message(
                userUid,
                preferenceRepository.getUserNickName(),
                profileUri,
                System.currentTimeMillis(),
                messageText
            )
            addChat(message)
        }
    }

    private fun addChat(message: Message) {
        viewModelScope.launch {
            val addChatCall = chatRepository.addChat(_postUid, message)
            when (addChatCall) {
                is ApiResultSuccess -> {
                    _isSendingComplete.value = Event(true)
                }
                is ApiResultError -> {
                    _isSendingComplete.value = Event(false)
                    Log.d(
                        "ChatRoomViewModel",
                        "Error code: ${addChatCall.code}, message: ${addChatCall.message}"
                    )
                }
                is ApiResultException -> {
                    _isSendingComplete.value = Event(false)
                    Log.d("ChatRoomViewModel", "Exception: ${addChatCall.throwable}")
                }
            }
        }
    }

    fun getAllChat() {
        viewModelScope.launch {
            val loadChatCall = chatRepository.getAllChat(_postUid)
            when (loadChatCall) {
                is ApiResultSuccess -> {
                    _isLoaded.value = Event(true)
                }
                is ApiResultError -> {
                    _isLoaded.value = Event(false)
                    Log.d(
                        "ChatRoomViewModel",
                        "Error code: ${loadChatCall.code}, message: ${loadChatCall.message}"
                    )
                }
                is ApiResultException -> {
                    _isLoaded.value = Event(false)
                    Log.d("ChatRoomViewModel", "Exception: ${loadChatCall.throwable}")
                }
            }
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