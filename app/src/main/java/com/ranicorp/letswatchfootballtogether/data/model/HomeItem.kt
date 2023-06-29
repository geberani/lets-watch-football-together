package com.ranicorp.letswatchfootballtogether.data.model

sealed class HomeItem

data class HomeHeader(
    val header: String
) : HomeItem()

data class HomeNewPost(
    val posts: List<Post>
) : HomeItem()

data class HomePopularPost(
    val posts: List<Post>
) : HomeItem()

data class HomeAllPost(
    val posts: List<Post>
) : HomeItem()
