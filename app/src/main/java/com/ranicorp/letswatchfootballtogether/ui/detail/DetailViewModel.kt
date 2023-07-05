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
    private val _participantsInfo = MutableLiveData<List<User>>()
    val participantsInfo: LiveData<List<User>> = _participantsInfo
    private val _isParticipated = MutableLiveData<Boolean>()
    val isParticipated: LiveData<Boolean> = _isParticipated
    private val userUid = userPreferenceRepository.getUserUid()

    fun getPostDetail(postUid: String) {
        viewModelScope.launch {
            _selectedPost.value = postRepository.getAllPosts().body()?.values?.flatMap { it.values }?.find { post ->
                post.postUid == postUid
            }
            participantsUidList.addAll(selectedPost.value?.participantsUidList ?: emptyList())
            getParticipantsDetail()
            isUserParticipated()
        }
    }

    private fun getParticipantsDetail() {
        viewModelScope.launch {
            _participantsInfo.value = userRepository.getAllUsers().body()?.values?.flatMap { it.values }?.filter { user ->
                user.uid in (selectedPost.value?.participantsUidList ?: emptyList<User>())
            }
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
            postRepository.updatePost(selectedPost.value?.postUid ?: TODO(), post ?: TODO())

            val user = userRepository.getUserInfo(userUid)
            user?.participatingEvent?.add(post.postUid)
            userRepository.updateUser(userUid, user ?: TODO())
        }

    }
}