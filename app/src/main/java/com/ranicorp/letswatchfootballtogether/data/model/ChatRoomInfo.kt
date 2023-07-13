package com.ranicorp.letswatchfootballtogether.data.model

data class ChatRoomInfo(
    val uid: String,
    val title: String,
    val lastMsg: String,
    val lastSentTime: String,
    val imageLocation: String
)