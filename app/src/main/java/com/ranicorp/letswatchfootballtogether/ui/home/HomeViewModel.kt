package com.ranicorp.letswatchfootballtogether.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.PostRepository
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val postRepository: PostRepository) : ViewModel() {

    private val _allPosts = MutableLiveData<Event<List<Post>>>()
    val allPosts: LiveData<Event<List<Post>>> = _allPosts
    private val _isLoadingComplete = MutableLiveData<Event<Boolean>>()
    val isLoadingComplete: LiveData<Event<Boolean>> = _isLoadingComplete

    fun loadAllPosts() {
        viewModelScope.launch {
            val loadAllPostsResult = postRepository.getAllPosts()
            when (loadAllPostsResult) {
                is ApiResultSuccess -> {
                    _allPosts.value =
                        Event(loadAllPostsResult.data?.values?.flatMap { it.values } ?: emptyList())
                    _isLoadingComplete.value = Event(true)
                }
                is ApiResultError -> {
                    _isLoadingComplete.value = Event(false)
                    Log.d(
                        "HomeViewModel",
                        "Error code: ${loadAllPostsResult.code}, message: ${loadAllPostsResult.message}"
                    )
                }
                is ApiResultException -> {
                    _isLoadingComplete.value = Event(false)
                    Log.d("HomeViewModel", "Exception: ${loadAllPostsResult.throwable}")
                }
            }
        }
    }
}