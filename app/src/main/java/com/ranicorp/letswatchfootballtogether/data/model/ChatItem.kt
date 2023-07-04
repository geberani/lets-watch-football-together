package com.ranicorp.letswatchfootballtogether.data.model

sealed class ChatItem

data class SentMessage(
    val message: Message
) : ChatItem()

data class ReceivedMessage(
    val message: Message
) : ChatItem()