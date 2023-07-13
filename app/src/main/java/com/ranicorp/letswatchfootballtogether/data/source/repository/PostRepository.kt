package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import javax.inject.Inject

class PostRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun addPost(postUid: String, post: Post): ApiResponse<Map<String, String>> {
        return remoteDataSource.addPost(postUid, post)
    }

    suspend fun getAllPosts(): ApiResponse<Map<String, Map<String, Post>>> {
        return remoteDataSource.getAllPosts()
    }

    suspend fun updatePost(postUid: String, firebaseUid: String, post: Post): ApiResponse<Post> {
        return remoteDataSource.updatePost(postUid, firebaseUid, post)
    }

    suspend fun getPost(postUid: String, firebaseUid: String): ApiResponse<Post> {
        return remoteDataSource.getPost(postUid, firebaseUid)
    }

    suspend fun getPostNoFirebaseUid(postUid: String): ApiResponse<Map<String, Post>> {
        return remoteDataSource.getPostNoFirebaseUid(postUid)
    }
}