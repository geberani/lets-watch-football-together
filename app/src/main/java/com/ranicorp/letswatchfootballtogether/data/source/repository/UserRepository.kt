package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun getUserNickNames(): Response<Map<String, String>> {
        return remoteDataSource.getUserNickNames()
    }

    suspend fun addUser(user: User): Response<Map<String, User>> {
        return remoteDataSource.addUser(user)
    }

    suspend fun addUserNickName(nickName: String): Response<Map<String, String>> {
        return remoteDataSource.addUserNickName(nickName)
    }

    suspend fun getAllUsers(): Response<Map<String, User>> {
        return remoteDataSource.getAllUsers()
    }
}