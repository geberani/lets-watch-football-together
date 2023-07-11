package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import javax.inject.Inject

class UserRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun getUserNickNames(): ApiResponse<Map<String, String>> {
        return remoteDataSource.getUserNickNames()
    }

    suspend fun addUser(uid: String, user: User): ApiResponse<Map<String, String>> {
        return remoteDataSource.addUser(uid, user)
    }

    suspend fun addUserNickName(nickName: String): ApiResponse<Map<String, String>> {
        return remoteDataSource.addUserNickName(nickName)
    }

    suspend fun getAllUsers(): ApiResponse<Map<String, Map<String, User>>> {
        return remoteDataSource.getAllUsers()
    }

    suspend fun updateUser(uid: String, firebaseUid: String, user: User): ApiResponse<User> {
        return remoteDataSource.updateUser(uid, firebaseUid, user)
    }

    suspend fun getUser(userUid: String, firebaseUid: String): ApiResponse<User> {
        return remoteDataSource.getUser(userUid, firebaseUid)
    }

    suspend fun getUserNoFirebaseUid(userUid: String): ApiResponse<Map<String, User>> {
        return remoteDataSource.getUserNoFirebaseUid(userUid)
    }
}