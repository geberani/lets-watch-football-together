package com.ranicorp.letswatchfootballtogether.data.source.local

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LocalDatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        var instance: AppDatabase? = null
        return instance ?: Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "footballapp-db"
        ).build().also {
            instance = it
        }
    }

    @Provides
    fun provideLocalDatabaseDataSource(appDatabase: AppDatabase): ChatRoomListDatabaseDataSource {
        return ChatRoomListDatabaseDataSource(appDatabase)
    }
}