package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.remote.onError
import com.ranicorp.letswatchfootballtogether.data.source.remote.onException
import com.ranicorp.letswatchfootballtogether.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class UserRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    fun getUserNickNames(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<List<String>> = flow {
        val response = remoteDataSource.getUserNickNames()
        response.onSuccess { data ->
            val names = data?.values?.toList() ?: listOf()
            emit(names)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun addUser(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        uid: String,
        user: User
    ): Flow<ApiResponse<Map<String, String>>> = flow {
        val response = remoteDataSource.addUser(uid, user)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun addUserNickName(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        nickName: String
    ): Flow<ApiResponse<Map<String, String>>> = flow {
        val response = remoteDataSource.addUserNickName(nickName)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getAllUsers(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<Map<String, Map<String, User>>> = flow {
        val response = remoteDataSource.getAllUsers()
        response.onSuccess { data ->
            if (data != null) {
                emit(data)
            }
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun updateUser(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        uid: String,
        firebaseUid: String,
        user: User
    ): Flow<ApiResponse<User>> = flow {
        val response = remoteDataSource.updateUser(uid, firebaseUid, user)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getUser(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        userUid: String,
        firebaseUid: String
    ): Flow<ApiResponse<User>> = flow {
        val response = remoteDataSource.getUser(userUid, firebaseUid)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getUserNoFirebaseUid(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        userUid: String
    ): Flow<Map<String, User>> = flow {
        val response = remoteDataSource.getUserNoFirebaseUid(userUid)
        response.onSuccess { data ->
            emit(data ?: mapOf<String, User>())
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun deleteUserNoFirebaseUid(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        userUid: String
    ): Flow<ApiResponse<Map<String, User>>> = flow {
        val response = remoteDataSource.deleteUserNoFirebaseUid(userUid)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)
}