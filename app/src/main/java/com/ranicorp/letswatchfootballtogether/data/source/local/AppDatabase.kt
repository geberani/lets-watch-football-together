package com.ranicorp.letswatchfootballtogether.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoom

@Database(entities = [ChatRoom::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun chatRoomListDao(): ChatRoomListDao
}