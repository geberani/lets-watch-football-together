package com.ranicorp.letswatchfootballtogether.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Post(
    val postUid: String,
    val writerUid: String,
    val createdTimeMillis: Long,
    val title: String,
    val location: String,
    val date: String,
    val time: String,
    val maxParticipants: Int,
    val description: String,
    val imageLocations: List<String>,
    val participantsUidList: MutableList<String>?
) {
    constructor() : this(
        "",
        "",
        0,
        "",
        "",
        "",
        "",
        0,
        "",
        emptyList(),
        mutableListOf()
    )
}