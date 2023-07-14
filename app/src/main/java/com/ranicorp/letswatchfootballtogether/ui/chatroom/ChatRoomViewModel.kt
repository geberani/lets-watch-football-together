package com.ranicorp.letswatchfootballtogether.ui.chatroom

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.ChildEventListener
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.*
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
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

    private val _allChat = MutableLiveData<Event<List<Message>>>()
    val allChat: LiveData<Event<List<Message>>> = _allChat
    private var postUid: String = ""
    val userUid = preferenceRepository.getUserUid()
    private val _isSendingComplete = MutableLiveData<Event<Boolean>>()
    val isSendingComplete: LiveData<Event<Boolean>> = _isSendingComplete
    private val _isLoaded = MutableLiveData<Event<Boolean>>()
    val isLoaded: LiveData<Event<Boolean>> = _isLoaded
    private var chatEventListener: ChildEventListener? = null
    private var profileUri = ""
    private var postTitle = ""
    private var postImageLocation = ""
    private var messageText = ""
    private var sentTime: Long = 0

    fun sendChat(messageText: String) {
        viewModelScope.launch {
            this@ChatRoomViewModel.messageText = messageText
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

            if (profileUri.isNotEmpty()) {
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
    }

    private fun addChat(message: Message) {
        viewModelScope.launch {
            val addChatCall = chatRepository.addChat(postUid, message)
            when (addChatCall) {
                is ApiResultSuccess -> {
                    getPostDetail()
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

    fun getPostDetail() {
        viewModelScope.launch {
            val getPostCall = postRepository.getPostNoFirebaseUid(postUid)
            when (getPostCall) {
                is ApiResultSuccess -> {
                    postTitle = getPostCall.data.values.first().title
                    postImageLocation = getPostCall.data.values.first().imageLocations.first()
                    addLatestChat()
                }
                is ApiResultError -> {
                    _isSendingComplete.value = Event(false)
                    Log.d(
                        "ChatRoomViewModel",
                        "Error code: ${getPostCall.code}, message: ${getPostCall.message}"
                    )
                }
                is ApiResultException -> {
                    _isSendingComplete.value = Event(false)
                    Log.d("ChatRoomViewModel", "Exception: ${getPostCall.throwable}")
                }
            }
        }
    }

    private fun addLatestChat() {
        viewModelScope.launch {
            val chatRoomInfo =
                ChatRoomInfo(userUid, postTitle, messageText, sentTime, postImageLocation)
            val addLatestChatCall = latestChatRepository.addLatestChat(postUid, chatRoomInfo)
            when (addLatestChatCall) {
                is ApiResultSuccess -> {
                    _isSendingComplete.value = Event(true)
                }
                is ApiResultError -> {
                    _isSendingComplete.value = Event(false)
                    Log.d(
                        "ChatRoomViewModel",
                        "Error code: ${addLatestChatCall.code}, message: ${addLatestChatCall.message}"
                    )
                }
                is ApiResultException -> {
                    _isSendingComplete.value = Event(false)
                    Log.d("ChatRoomViewModel", "Exception: ${addLatestChatCall.throwable}")
                }
            }
        }
    }

    fun getAllChat() {
        viewModelScope.launch {
            val loadChatCall = chatRepository.getAllChat(postUid)
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
        this.postUid = postUid
    }

    fun addChatEventListener() {
        chatEventListener = chatRepository.addChatEventListener(postUid) { chatItem ->
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