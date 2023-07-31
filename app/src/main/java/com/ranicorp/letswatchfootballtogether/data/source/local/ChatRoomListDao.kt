package com.ranicorp.letswatchfootballtogether.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoom

@Dao
interface ChatRoomListDao {

    @Insert
    suspend fun insert(chatRoom: ChatRoom)

    @Query("SELECT * FROM chat_room")
    suspend fun getAllChatRoom(): List<ChatRoom>?

    @Query("DELETE FROM chat_room")
    suspend fun deleteAllChatRooms()
}