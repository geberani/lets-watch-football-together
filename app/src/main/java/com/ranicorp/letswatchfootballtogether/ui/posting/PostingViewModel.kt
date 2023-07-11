package com.ranicorp.letswatchfootballtogether.ui.posting

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import com.ranicorp.letswatchfootballtogether.util.DateFormatText.getCurrentDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class PostingViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    val title = MutableLiveData<String>()
    val location = MutableLiveData<String>()
    val date = MutableLiveData<String>()
    val time = MutableLiveData<String>()
    val maxParticipants = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    private val _errorMsgResId = MutableLiveData<Event<Int>>()
    val errorMsgResId: LiveData<Event<Int>> = _errorMsgResId
    private val _imageUriList: MutableLiveData<Event<MutableList<Uri>>> = MutableLiveData()
    val imageUriList: LiveData<Event<MutableList<Uri>>> = _imageUriList
    private val isPostAdded = MutableLiveData(Event(false))
    private val _isLoading = MutableLiveData(Event(false))
    val isLoading: LiveData<Event<Boolean>> = _isLoading
    private val _isComplete: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isComplete: LiveData<Event<Boolean>> = _isComplete
    private val userUid = userPreferenceRepository.getUserUid()

    fun addImage(uri: Uri) {
        _imageUriList.value?.content?.add(uri)
    }

    fun removeImage(uri: Uri) {
        _imageUriList.value?.content?.minus(uri)
    }

    fun complete() {
        if (isNotValidInfo(imageUriList.value.toString(), R.string.guide_message_set_image)) return
        if (isNotValidInfo(title.value, R.string.guide_message_set_title)) return
        if (isNotValidInfo(location.value, R.string.guide_message_set_location)) return
        if (isNotValidInfo(date.value, R.string.guide_message_set_date)) return
        if (isNotValidInfo(time.value, R.string.guide_message_set_time)) return
        if (isNotValidInfo(
                maxParticipants.value,
                R.string.guide_message_set_max_participants
            )
        ) return
        if (isNotValidInfo(description.value, R.string.guide_message_set_description)) return

        addPost()
    }

    private fun isNotValidInfo(text: String?, messageResId: Int): Boolean {
        if (text.isNullOrBlank() || text == "null") {
            _errorMsgResId.value = Event(messageResId)
            return true
        }
        return false
    }

    private fun addPost() {
        viewModelScope.launch {
            _isLoading.value = Event(true)
            val imageLocations = addImageToStorage(_imageUriList.value?.content ?: emptyList())
            val postUid = userUid + userUid + System.currentTimeMillis()
            val post = Post(
                postUid,
                userUid,
                System.currentTimeMillis(),
                title.value!!,
                location.value!!,
                date.value!!,
                time.value!!,
                maxParticipants.value!!.toInt(),
                description.value!!,
                imageLocations,
                mutableListOf(userUid)
            )
            addPostCall(postUid, post)
            if (isPostAdded.value?.content != true) {
                _isLoading.value = Event(false)
                _isComplete.value = Event(false)
                return@launch
            }
            val userInfo = getUserInfoCall()
            if (userInfo.isNullOrEmpty()) {
                _isLoading.value = Event(false)
                _isComplete.value = Event(false)
                return@launch
            }
            updateUserCall(userInfo, postUid)
            //TODO() 해당 게시물 채팅방 생성
        }
    }

    private suspend fun getUserInfoCall(): Map<String, User>? {
        val callResponse = userRepository.getUserNoFirebaseUid(userUid)
        when (callResponse) {
            is ApiResultSuccess -> {
                return callResponse.data
            }
            is ApiResultError -> {
                Log.d(
                    "PostingViewModel",
                    "Error code: ${callResponse.code}, message: ${callResponse.message}"
                )
                return null
            }
            is ApiResultException -> {
                Log.d("PostingViewModel", "Exception: ${callResponse.throwable}")
                return null
            }
        }
    }

    private suspend fun updateUserCall(userInfo: Map<String, User>, postUid: String) {
        val firebaseUid = userInfo.keys.first()
        val user = userInfo.values.first()
        user.participatingEvent.add(postUid)
        val updateUserResult = userRepository.updateUser(userUid, firebaseUid, user)
        when (updateUserResult) {
            is ApiResultSuccess -> {
                _isLoading.value = Event(false)
                _isComplete.value = Event(true)
            }
            is ApiResultError -> {
                _isLoading.value = Event(false)
                _isComplete.value = Event(false)
                Log.d(
                    "PostingViewModel",
                    "Error code: ${updateUserResult.code}, message: ${updateUserResult.message}"
                )
            }
            is ApiResultException -> {
                _isLoading.value = Event(false)
                _isComplete.value = Event(false)
                Log.d("PostingViewModel", "Exception: ${updateUserResult.throwable}")
            }
        }
    }


    private suspend fun addPostCall(postUid: String, post: Post) {
        val addPostResult = postRepository.addPost(postUid, post)
        when (addPostResult) {
            is ApiResultSuccess -> {
                isPostAdded.value = Event(true)
            }
            is ApiResultError -> {
                Log.d(
                    "PostingViewModel",
                    "Error code: ${addPostResult.code}, message: ${addPostResult.message}"
                )
            }
            is ApiResultException -> {
                Log.d("PostingViewModel", "Exception: ${addPostResult.throwable}")
            }
        }
    }

    private suspend fun addImageToStorage(imageList: List<Uri>): List<String> = coroutineScope {
        imageList.map { imageUri ->
            val location = "images/${imageUri}" + getCurrentDateString()
            val imageRef = firebaseStorage.getReference(location)
            imageRef.putFile(imageUri).await()
            location
        }
    }
}