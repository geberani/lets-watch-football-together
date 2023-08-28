package com.ranicorp.letswatchfootballtogether.ui.posting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.ImageContent
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.util.DateFormatText.getCurrentDateString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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

    val title = MutableStateFlow<String>("")
    val location = MutableStateFlow<String>("")
    val date = MutableStateFlow<String>("")
    val time = MutableStateFlow<String>("")
    val maxParticipants = MutableStateFlow<String>("")
    val description = MutableStateFlow<String>("")
    private val _errorMsgResId = MutableStateFlow<Int?>(null)
    val errorMsgResId: StateFlow<Int?> = _errorMsgResId
    private var imageList: List<ImageContent> = emptyList()
    private val isPostAdded = MutableStateFlow(false)
    private val _isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    private val _isComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isComplete: StateFlow<Boolean> = _isComplete
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

        clickAddPostBtn()
    }

    private fun isNotValidInfo(text: String?, messageResId: Int): Boolean {
        if (text.isNullOrBlank() || text == "null" || text == "[]") {
            _errorMsgResId.value = messageResId
            return true
        }
        return false
    }

    private fun clickAddPostBtn() {
        viewModelScope.launch {
            _isLoading.value = true
            userRepository.getUserNoFirebaseUid(
                onComplete = { addPost() },
                onError = {
                    _isLoading.value = false
                    _isComplete.value = false
                    _errorMsgResId.value = R.string.error_message_post_failed
                },
                userUid
            ).collect { data ->
                userInfo = data

            }
        }
    }

    private fun addPost() {
        viewModelScope.launch {
            val imageLocations = addImageToStorage(imageList)
            val postUid = userUid + userUid + System.currentTimeMillis()
            val post = Post(
                postUid,
                userUid,
                System.currentTimeMillis(),
                title.value,
                location.value,
                date.value,
                time.value,
                maxParticipants.value.toInt(),
                description.value,
                imageLocations,
                mutableListOf(userUid)
            )
            postRepository.addPost(
                onComplete = {
                    isPostAdded.value = true
                    updateUserCall(postUid)
                },
                onError = {
                    _isLoading.value = false
                    _isComplete.value = false
                }, postUid, post
            ).collect {

            }
        }
    }

    private fun updateUserCall(postUid: String) {
        viewModelScope.launch {
            val firebaseUid = userInfo.keys.first()
            val user = userInfo.values.first()
            user.participatingEvent.add(postUid)
            userRepository.updateUser(
                onComplete = {
                    _isLoading.value = false
                    _isComplete.value = true
                },
                onError = {
                    _isLoading.value = false
                    _isComplete.value = false
                    _errorMsgResId.value = R.string.error_message_post_failed
                },
                userUid, firebaseUid, user
            ).collect { }
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

    fun removeSelectedImage(imageContent: ImageContent) {
        updateImageList(imageList.toMutableList().apply{ remove(imageContent) })
    }
}