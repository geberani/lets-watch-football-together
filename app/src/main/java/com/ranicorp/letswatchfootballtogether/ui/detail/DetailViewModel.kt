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

    fun getPostDetail(postUid: String) {
        viewModelScope.launch {
            val allPosts = postRepository.getAllPosts().body()?.values?.toList() ?: emptyList()
            _selectedPost.value = allPosts.find { post ->
                post.postUid == postUid
            } ?: TODO()
            participantsUidList.addAll(selectedPost.value?.participantsUidList ?: TODO())
            getParticipantsDetail()
            isUserParticipated()
        }
    }

    private fun getParticipantsDetail() {
        viewModelScope.launch {
            val allUsers = userRepository.getAllUsers().body()?.values?.toList() ?: emptyList()
            _participantsInfo.value = allUsers.filter { user ->
                user.uid in (selectedPost.value?.participantsUidList ?: TODO())
            }
        }
    }

    private fun isUserParticipated() {
        _isParticipated.value =
            participantsUidList.contains(userPreferenceRepository.getUserUid())
    }
}