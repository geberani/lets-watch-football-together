package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun addPost(postUid: String, post: Post): Response<Map<String, Map<String, Post>>> {
        return remoteDataSource.addPost(postUid, post)
    }

    suspend fun getAllPosts(): Response<Map<String, Map<String, Post>>> {
        return remoteDataSource.getAllPosts()
    }

    suspend fun updatePost(postUid: String, post: Post): Response<Map<String, Map<String, Post>>> {
        return remoteDataSource.updatePost(postUid, post)
    }
}