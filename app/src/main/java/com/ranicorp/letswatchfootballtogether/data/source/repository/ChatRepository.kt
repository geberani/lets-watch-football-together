package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.google.firebase.database.ChildEventListener
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import javax.inject.Inject

class ChatRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun addChat(postUid: String, message: Message): ApiResponse<Map<String, String>> {
        return remoteDataSource.addChat(postUid, message)
    }

    suspend fun getAllChat(postUid:String): ApiResponse<Map<String, Message>> {
        return remoteDataSource.getAllChat(postUid)
    }

    fun addChatEventListener(chatRoomUid: String, onChatItemAdded: (Message) -> Unit): ChildEventListener {
        return remoteDataSource.addChatEventListener(chatRoomUid, onChatItemAdded)
    }
}