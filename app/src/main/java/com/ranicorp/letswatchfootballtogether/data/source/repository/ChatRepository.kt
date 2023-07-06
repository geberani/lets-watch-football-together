package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import retrofit2.Response
import javax.inject.Inject

class ChatRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun addChat(postUid: String, message: Message): Response<Map<String, String>> {
        return remoteDataSource.addChat(postUid, message)
    }

    suspend fun getAllChat(postUid:String): Response<Map<String, Message>> {
        return remoteDataSource.getAllChat(postUid)
    }
}