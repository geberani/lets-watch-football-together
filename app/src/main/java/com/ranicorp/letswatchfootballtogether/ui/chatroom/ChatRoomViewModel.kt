package com.ranicorp.letswatchfootballtogether.ui.chatroom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.repository.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatRoomViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val preferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val latestChatRepository: LatestChatRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    private val _allChat = MutableStateFlow<List<Message>>(emptyList())
    val allChat: StateFlow<List<Message>> = _allChat
    private var postUid: String = ""
    val userUid = preferenceRepository.getUserUid()
    private val isSendingComplete: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    private val _isLoaded: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val isLoaded: StateFlow<Boolean?> = _isLoaded
    private var chatEventListener: ChildEventListener? = null
    private var profileUri = ""
    private var postTitle = ""
    private var postImageLocation = ""
    private var messageText = ""
    private var sentTime: Long = 0

    fun getUserDetail() {
        viewModelScope.launch {
            if (profileUri.isEmpty()) {
                viewModelScope.launch {
                    userRepository.getUserNoFirebaseUid(
                        onComplete = { },
                        onError = { isSendingComplete.value = false },
                        userUid
                    ).collect { response ->
                        val user = response.values.first()
                        profileUri = user.profileUri
                    }
                }
            }
        }
    }

    fun sendChat(messageText: String) {
        viewModelScope.launch {
            this@ChatRoomViewModel.messageText = messageText
            if (profileUri.isEmpty()) {
                getUserDetail()
            }

            sentTime = System.currentTimeMillis()
            val message = Message(
                userUid,
                preferenceRepository.getUserNickName(),
                profileUri,
                sentTime,
                messageText
            )
            addChat(message)
        }
    }

    private fun addChat(message: Message) {
        viewModelScope.launch {
            chatRepository.addChat(
                onComplete = { getPostDetail() },
                onError = { isSendingComplete.value = false },
                postUid, message
            ).collect { }
        }
    }

    private fun getPostDetail() {
        viewModelScope.launch {
            postRepository.getPostNoFirebaseUid(
                onComplete = { addLatestChat() },
                onError = { isSendingComplete.value = false },
                postUid
            ).collect { response ->
                postTitle = response.values.first().title
                postImageLocation = response.values.first().imageLocations.first()
            }
        }
    }

    private fun addLatestChat() {
        viewModelScope.launch {
            val chatRoomInfo =
                ChatRoomInfo(postUid, postTitle, messageText, sentTime, postImageLocation)
            latestChatRepository.addLatestChat(
                onComplete = { isSendingComplete.value = true },
                onError = { isSendingComplete.value = false },
                postUid, chatRoomInfo
            ).collect { }
        }
    }

    fun getAllChat() {
        viewModelScope.launch {
            chatRepository.getAllChat(
                onComplete = { _isLoaded.value = true },
                onError = { _isLoaded.value = false },
                postUid
            ).collect { }
        }
    }

    fun setPostUid(postUid: String) {
        this.postUid = postUid
    }

    fun addChatEventListener() {
        viewModelScope.launch {
            chatEventListener = chatRepository.addChatEventListener(postUid) { chatItem ->
                val currentList = _allChat.value
                val newList = currentList.toMutableList().apply { add(chatItem) }
                _allChat.value = newList
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        chatEventListener = null
    }
}