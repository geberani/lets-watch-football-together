package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.ranicorp.letswatchfootballtogether.data.model.User
import retrofit2.Response

class RemoteDataSource(private val apiClient: ApiClient) {

    suspend fun getUserNickNames(): Response<Map<String, String>> {
        return apiClient.getUserNickNames()
    }

    suspend fun addUserNickName(userNickName: String): Response<Map<String, String>> {
        return apiClient.addUserNickName(userNickName)
    }

    suspend fun addUser(user: User): Response<Map<String, String>> {
        return apiClient.addUser(user)
    }
}