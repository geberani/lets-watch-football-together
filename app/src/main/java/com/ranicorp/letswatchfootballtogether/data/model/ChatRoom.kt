package com.ranicorp.letswatchfootballtogether.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_room")
data class ChatRoom(
    @PrimaryKey val uid: String,
    val title: String,
    @ColumnInfo(name = "last_msg") val lastMsg: String,
    @ColumnInfo(name = "last_sent_time") val lastSentTime: Long,
    @ColumnInfo(name = "image_location") val imageLocation: String
)