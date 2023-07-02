package com.ranicorp.letswatchfootballtogether.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class User(
    val uid: String,
    val nickName: String,
    val profileUri: String,
    val participatingEvent: MutableList<String>?
)