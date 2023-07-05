package com.ranicorp.letswatchfootballtogether.data.model

data class Message(
    val senderUid: String,
    val senderNickname: String,
    val senderProfileLocation: String,
    val sentTimeMillis: Long,
    val content: String
)