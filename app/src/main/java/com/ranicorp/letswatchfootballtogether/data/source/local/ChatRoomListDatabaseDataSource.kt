package com.ranicorp.letswatchfootballtogether.data.source.local

import com.ranicorp.letswatchfootballtogether.data.model.ChatRoom
import javax.inject.Inject

class ChatRoomListDatabaseDataSource @Inject constructor(private val appDatabase: AppDatabase) {

    suspend fun insert(chatRoom: ChatRoom) {
        return appDatabase.chatRoomListDao().insert(chatRoom)
    }

    suspend fun getAllChatRoom(): List<ChatRoom>? {
        return appDatabase.chatRoomListDao().getAllChatRoom()
    }

    suspend fun deleteAllChatRooms() {
        return appDatabase.chatRoomListDao().deleteAllChatRooms()
    }
}