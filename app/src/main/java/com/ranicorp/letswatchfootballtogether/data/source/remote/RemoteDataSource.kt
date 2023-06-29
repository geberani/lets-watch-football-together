package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiClient: ApiClient) {

    suspend fun getUserNickNames(): Response<Map<String, String>> {
        return apiClient.getUserNickNames()
    }

    suspend fun addUserNickName(userNickName: String): Response<Map<String, String>> {
        return apiClient.addUserNickName(userNickName)
    }

    suspend fun addUser(user: User): Response<Map<String, User>> {
        return apiClient.addUser(user)
    }

    suspend fun addPost(post: Post): Response<Map<String, String>> {
        return apiClient.addPost(post)
    }

    suspend fun getAllPosts(): Response<Map<String, Post>> {
        return apiClient.getAllPosts()
    }
}