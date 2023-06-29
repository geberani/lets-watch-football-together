package com.ranicorp.letswatchfootballtogether.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PostRepository) : ViewModel() {

    private val _allPosts = MutableLiveData<List<Post>>()
    val allPosts: LiveData<List<Post>> = _allPosts

    fun loadAllPosts() {
        viewModelScope.launch {
            _allPosts.value = repository.getAllPosts().body()?.values?.toList() ?: emptyList()
        }
    }

    companion object {
        fun provideFactory(repository: PostRepository) = viewModelFactory {
            initializer {
                HomeViewModel(repository)
            }
        }
    }
}