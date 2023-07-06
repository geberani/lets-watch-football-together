package com.ranicorp.letswatchfootballtogether.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Message(
    val senderUid: String,
    val senderNickname: String,
    val senderProfileLocation: String,
    val sentTimeMillis: Long,
    val content: String
)