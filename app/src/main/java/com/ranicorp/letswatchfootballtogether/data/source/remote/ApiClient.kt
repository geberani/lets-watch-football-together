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

    @POST("users/{uid}.json")
    suspend fun addUser(@Path("uid") uid: String, @Body user: User): Response<Map<String, Map<String, User>>>

    @POST("posts/{postUid}.json")
    suspend fun addPost(@Path("postUid") postUid: String, @Body post: Post): Response<Map<String, Map<String, Post>>>

    @GET("posts.json")
    suspend fun getAllPosts(): Response<Map<String, Map<String, Post>>>

    @GET("users.json")
    suspend fun getAllUsers(): Response<Map<String, Map<String, User>>>
}