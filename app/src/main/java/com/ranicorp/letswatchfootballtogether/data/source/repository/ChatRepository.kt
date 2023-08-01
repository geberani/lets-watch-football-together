package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.google.firebase.database.ChildEventListener
import com.ranicorp.letswatchfootballtogether.data.model.Message
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

class ChatRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) {

    fun addChat(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String,
        message: Message
    ): Flow<ApiResponse<Map<String, String>>> = flow {
        val response = remoteDataSource.addChat(postUid, message)
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


    fun getAllChat(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String
    ): Flow<ApiResponse<Map<String, Message>>> = flow {
        val response = remoteDataSource.getAllChat(postUid)
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

    fun addChatEventListener(
        chatRoomUid: String,
        onChatItemAdded: (Message) -> Unit
    ): ChildEventListener {
        return remoteDataSource.addChatEventListener(chatRoomUid, onChatItemAdded)
    }
}