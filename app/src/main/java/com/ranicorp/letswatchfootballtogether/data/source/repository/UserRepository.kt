package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class UserRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun getUserNickNames(): Response<Map<String, String>> {
        return remoteDataSource.getUserNickNames()
    }

    suspend fun addUser(uid: String, user: User): Response<Map<String, String>> {
        return remoteDataSource.addUser(uid, user)
    }

    suspend fun addUserNickName(nickName: String): Response<Map<String, String>> {
        return remoteDataSource.addUserNickName(nickName)
    }

    suspend fun getAllUsers(): Response<Map<String, Map<String, User>>> {
        return remoteDataSource.getAllUsers()
    }

    suspend fun updateUser(uid: String, firebaseUid: String, user: User): Response<User> {
        return remoteDataSource.updateUser(uid, firebaseUid, user)
    }

    suspend fun getUserInfo(userUid: String): User? {
        val user = getAllUsers().body()?.values?.flatMap { it.values }?.find {
            it.uid == userUid
        }
        return user
    }

    suspend fun getUser(userUid: String, firebaseUid: String): Response<User> {
        return remoteDataSource.getUser(userUid, firebaseUid)
    }

    suspend fun getUserNoFirebaseUid(userUid: String): Response<Map<String, User>> {
        return remoteDataSource.getUserNoFirebaseUid(userUid)
    }
}