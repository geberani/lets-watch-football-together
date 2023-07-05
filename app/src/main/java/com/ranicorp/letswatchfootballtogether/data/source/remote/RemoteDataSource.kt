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

    suspend fun addUser(uid: String, user: User): Response<Map<String, Map<String, User>>> {
        return apiClient.addUser(uid, user)
    }

    suspend fun addPost(postUid: String, post: Post): Response<Map<String, Map<String, Post>>> {
        return apiClient.addPost(postUid, post)
    }

    suspend fun getAllPosts(): Response<Map<String, Map<String, Post>>> {
        return apiClient.getAllPosts()
    }

    suspend fun getAllUsers(): Response<Map<String, Map<String, User>>> {
        return apiClient.getAllUsers()
    }

    suspend fun updatePost(postUid: String, firebaseUid: String, post: Post): Response<Post> {
        return apiClient.updatePost(postUid, firebaseUid, post)
    }

    suspend fun updateUser(uid: String, firebaseUid: String, user: User): Response<User> {
        return apiClient.updateUser(uid, firebaseUid, user)
    }

    suspend fun getPost(postUid: String, firebaseUid: String): Response<Post> {
        return apiClient.getPost(postUid, firebaseUid)
    }

    suspend fun getPostNoFirebaseUid(postUid: String): Response<Map<String, Post>> {
        return apiClient.getPostNoFirebaseUid(postUid)
    }
}