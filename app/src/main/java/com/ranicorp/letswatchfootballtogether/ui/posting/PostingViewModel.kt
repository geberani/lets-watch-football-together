package com.ranicorp.letswatchfootballtogether.ui.posting

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.R
import com.ranicorp.letswatchfootballtogether.data.model.Post
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
    private val _isLoading = MutableLiveData(Event(false))
    val isLoading: LiveData<Event<Boolean>> = _isLoading
    private val _isComplete = MutableLiveData(Event(false))
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
            if (postRepository.addPost(postUid, post).isSuccessful) {
                val userResponse = userRepository.getUserNoFirebaseUid(userUid)
                val firebaseUid = userResponse.body()?.keys?.first() ?: ""
                val user = userResponse.body()?.values?.first()
                user?.participatingEvent?.add(post.postUid)
                userRepository.updateUser(userUid, firebaseUid, user ?: TODO())
                _isLoading.value = Event(false)
                _isComplete.value = Event(true)
            }
            //TODO 해당 게시물 채팅방 생성, User의 ParticipatingEvent List에 위 이벤트 추가
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