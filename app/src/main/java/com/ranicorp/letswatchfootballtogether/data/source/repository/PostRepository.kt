package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import retrofit2.Response

class PostRepository(private val remoteDataSource: RemoteDataSource) {

    suspend fun addPost(post: Post): Response<Map<String, String>> {
        return remoteDataSource.addPost(post)
    }
}