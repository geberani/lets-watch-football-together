package com.ranicorp.letswatchfootballtogether.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val userRepository: UserRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _selectedPost = MutableLiveData<Post>()
    val selectedPost: LiveData<Post> = _selectedPost
    private val participantsUidList = mutableListOf<String>()
    private val _participantsInfo = MutableLiveData<MutableList<User>>()
    val participantsInfo: LiveData<MutableList<User>> = _participantsInfo
    private val _isParticipated = MutableLiveData<Boolean>()
    val isParticipated: LiveData<Boolean> = _isParticipated
    private val userUid = userPreferenceRepository.getUserUid()
    private lateinit var firebaseUid: String

    fun getPostDetail(postUid: String) {
        viewModelScope.launch {
            val postResponse = postRepository.getPostNoFirebaseUid(postUid).body()
            firebaseUid = postResponse?.keys?.first() ?: TODO()
            _selectedPost.value = postResponse.values.first()
            participantsUidList.addAll(selectedPost.value?.participantsUidList ?: emptyList())
            getParticipantsDetail()
            isUserParticipated()
        }
    }

    private fun getParticipantsDetail() {
        viewModelScope.launch {
            val participantsList = userRepository.getAllUsers().body()?.values?.flatMap { it.values }?.filter { user ->
                user.uid in (selectedPost.value?.participantsUidList ?: emptyList<User>())
            }
            _participantsInfo.value?.addAll(participantsList ?: emptyList())
        }
    }

    private fun isUserParticipated() {
        _isParticipated.value =
            participantsUidList.contains(userUid)
    }

    fun participate() {
        viewModelScope.launch {
            val post = _selectedPost.value
            post?.participantsUidList?.add(userUid)
            postRepository.updatePost(selectedPost.value?.postUid ?: TODO(), firebaseUid, post ?: TODO())

            val user = userRepository.getAllUsers().body()?.values?.flatMap { it.values }?.find {
                it.uid == userUid
            }
            user?.participatingEvent?.add(post.postUid)
            userRepository.updateUser(userUid, firebaseUid, user ?: TODO())

            participantsUidList.add(userUid)
            _participantsInfo.value?.add(user)
            _isParticipated.value = true
        }
    }
}