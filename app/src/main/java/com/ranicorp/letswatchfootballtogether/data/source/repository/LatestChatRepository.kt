package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
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

class LatestChatRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    fun addLatestChat(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String,
        chatRoomInfo: ChatRoomInfo
    ): Flow<ApiResponse<ChatRoomInfo>> = flow {
        val response = remoteDataSource.addLatestChat(postUid, chatRoomInfo)
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

    fun getLatestChat(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String
    ): Flow<ChatRoomInfo?> = flow {
        val response = remoteDataSource.getLatestChat(postUid)
        response.onSuccess { data ->
            emit(data)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)
}