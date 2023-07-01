package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiClient {

    @GET("userNickNames.json")
    suspend fun getUserNickNames(): Response<Map<String, String>>

    @POST("userNickNames.json")
    suspend fun addUserNickName(@Body userNickName: String): Response<Map<String, String>>

    @POST("users.json")
    suspend fun addUser(@Body user: User): Response<Map<String, User>>

    @POST("posts.json")
    suspend fun addPost(@Body post: Post): Response<Map<String, String>>

    @GET("posts.json")
    suspend fun getAllPosts(): Response<Map<String, Post>>

    @GET("users.json")
    suspend fun getAllUsers(): Response<Map<String, User>>
}