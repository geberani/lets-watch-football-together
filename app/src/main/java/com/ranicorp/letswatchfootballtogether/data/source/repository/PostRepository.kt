package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class PostRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun addPost(post: Post): Response<Map<String, String>> {
        return remoteDataSource.addPost(post)
    }

    suspend fun getAllPosts(): Response<Map<String, Post>> {
        return remoteDataSource.getAllPosts()
    }
}