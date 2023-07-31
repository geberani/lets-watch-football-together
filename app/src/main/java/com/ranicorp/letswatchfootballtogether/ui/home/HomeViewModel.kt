package com.ranicorp.letswatchfootballtogether.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

    val allPosts: StateFlow<List<Post>> = transformPostList().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _isLoadingComplete: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val isLoadingComplete: StateFlow<Boolean> = _isLoadingComplete

    private fun transformPostList(): Flow<List<Post>> = postRepository.getAllPosts(
        onComplete = { _isLoadingComplete.value = true },
        onError = { _isLoadingComplete.value = false }
    ).map { data ->
        data
    }
}