package com.ranicorp.letswatchfootballtogether.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

    private val _allPosts = MutableLiveData<List<Post>>()
    val allPosts: LiveData<List<Post>> = _allPosts

    fun loadAllPosts() {
        viewModelScope.launch {
            _allPosts.value = postRepository.getAllPosts().body()?.values?.toList() ?: emptyList()
        }
    }
}