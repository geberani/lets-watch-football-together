package com.ranicorp.letswatchfootballtogether.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChatRoomInfo(
    val uid: String,
    val title: String,
    val lastMsg: String,
    val lastSentTime: Long,
    val imageLocation: String
)