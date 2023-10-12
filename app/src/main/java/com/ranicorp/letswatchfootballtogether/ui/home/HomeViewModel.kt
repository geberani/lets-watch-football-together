package com.ranicorp.letswatchfootballtogether.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

    private val _allPosts = MutableStateFlow<List<Post>>(emptyList())
    val allPosts: StateFlow<List<Post>> = _allPosts
    private val _isLoadingComplete: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoadingComplete: StateFlow<Boolean> = _isLoadingComplete

    fun loadAllPosts() {
        viewModelScope.launch {
            postRepository.getAllPosts(
                onComplete = { _isLoadingComplete.value = true },
                onError = { _isLoadingComplete.value = false }
            ).collect { response ->
                val filteredPosts = response.map { post ->
                    post.participantsUidList?.remove("")
                    post
                }
                _allPosts.value = filteredPosts
            }
        }
    }
}