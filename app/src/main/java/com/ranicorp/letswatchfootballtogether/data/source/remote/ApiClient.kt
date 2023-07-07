package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import retrofit2.Response
import retrofit2.http.*

interface ApiClient {

    @GET("userNickNames.json")
    suspend fun getUserNickNames(): Response<Map<String, String>>

    @POST("userNickNames.json")
    suspend fun addUserNickName(@Body userNickName: String): Response<Map<String, String>>

    @POST("users/{uid}.json")
    suspend fun addUser(@Path("uid") uid: String, @Body user: User): Response<Map<String, String>>

    @POST("posts/{postUid}.json")
    suspend fun addPost(@Path("postUid") postUid: String, @Body post: Post): Response<Map<String, String>>

    @GET("posts.json")
    suspend fun getAllPosts(): Response<Map<String, Map<String, Post>>>

    @GET("users.json")
    suspend fun getAllUsers(): Response<Map<String, Map<String, User>>>

    @PUT("posts/{postUid}/{firebaseUid}.json")
    suspend fun updatePost(@Path("postUid") postUid: String, @Path("firebaseUid") firebaseUid: String, @Body post: Post): Response<Post>

    @PUT("users/{uid}/{firebaseUid}.json")
    suspend fun updateUser(@Path("uid") uid: String, @Path("firebaseUid") firebaseUid: String, @Body user: User): Response<User>

    @GET("posts/{postUid}/{firebaseUid}.json")
    suspend fun getPost(@Path("postUid") postUid: String, @Path("firebaseUid") firebaseUid: String): Response<Post>

    @GET("posts/{postUid}.json")
    suspend fun getPostNoFirebaseUid(@Path("postUid") postUid: String): Response<Map<String, Post>>

    @POST("chat/{postUid}.json")
    suspend fun addChat(@Path("postUid") postUid: String, @Body message: Message): Response<Map<String, String>>

    @GET("chat/{postUid}.json")
    suspend fun getAllChat(@Path("postUid") postUid: String): Response<Map<String, Message>>

    @GET("users/{userUid}/{firebaseUid}.json")
    suspend fun getUser(@Path("userUid") userUid: String, @Path("firebaseUid") firebaseUid: String): Response<User>

    @GET("users/{userUid}.json")
    suspend fun getUserNoFirebaseUid(@Path("userUid") userUid: String): Response<Map<String, User>>
}