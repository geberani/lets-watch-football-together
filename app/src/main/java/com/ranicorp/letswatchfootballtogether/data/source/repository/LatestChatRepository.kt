package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import javax.inject.Inject

class LatestChatRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    suspend fun addLatestChat(postUid: String, chatRoomInfo: ChatRoomInfo): ApiResponse<ChatRoomInfo> {
        return remoteDataSource.addLatestChat(postUid, chatRoomInfo)
    }
}