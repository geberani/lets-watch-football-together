package com.ranicorp.letswatchfootballtogether.ui.posting

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.ImageContent
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
    private var imageList: List<ImageContent> = emptyList()
    private val isPostAdded = MutableLiveData(Event(false))
    private val _isLoading: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isLoading: LiveData<Event<Boolean>> = _isLoading
    private val _isComplete: MutableLiveData<Event<Boolean>> = MutableLiveData()
    val isComplete: LiveData<Event<Boolean>> = _isComplete
    private val userUid = userPreferenceRepository.getUserUid()
    private var userInfo: Map<String, User> = emptyMap()

    fun updateImageList(items: List<ImageContent>) {
        imageList = items
    }

    fun complete() {
        if (isNotValidInfo(imageList.toString(), R.string.guide_message_set_image)) return
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
            val callResponse = userRepository.getUserNoFirebaseUid(userUid)
            when (callResponse) {
                is ApiResultSuccess -> {
                    userInfo = callResponse.data
                    addPostCall()
                }
                is ApiResultError -> {
                    _isLoading.value = Event(false)
                    _isComplete.value = Event(false)
                    Log.d(
                        "PostingViewModel",
                        "Error code: ${callResponse.code}, message: ${callResponse.message}"
                    )
                }
                is ApiResultException -> {
                    _isLoading.value = Event(false)
                    _isComplete.value = Event(false)
                    Log.d("PostingViewModel", "Exception: ${callResponse.throwable}")
                }
            }
        }
    }

    private suspend fun updateUserCall(postUid: String) {
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


    private suspend fun addPostCall() {
        val imageLocations = addImageToStorage(imageList)
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
        val addPostResult = postRepository.addPost(postUid, post)
        when (addPostResult) {
            is ApiResultSuccess -> {
                isPostAdded.value = Event(true)
                updateUserCall(postUid)
            }
            is ApiResultError -> {
                _isLoading.value = Event(false)
                _isComplete.value = Event(false)
                Log.d(
                    "PostingViewModel",
                    "Error code: ${addPostResult.code}, message: ${addPostResult.message}"
                )
            }
            is ApiResultException -> {
                _isLoading.value = Event(false)
                _isComplete.value = Event(false)
                Log.d("PostingViewModel", "Exception: ${addPostResult.throwable}")
            }
        }
    }

    private suspend fun addImageToStorage(imageList: List<ImageContent>): List<String> =
        coroutineScope {
            imageList.map { imageUri ->
                val location = "images/${imageUri}" + getCurrentDateString()
                val imageRef = firebaseStorage.getReference(location)
                imageRef.putFile(imageUri.uri).await()
                location
            }
        }
}