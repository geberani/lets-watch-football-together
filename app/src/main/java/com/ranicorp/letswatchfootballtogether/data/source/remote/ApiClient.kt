package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import retrofit2.http.*

interface ApiClient {

    @GET("userNickNames.json")
    suspend fun getUserNickNames(): ApiResponse<Map<String, String>>

    @POST("userNickNames.json")
    suspend fun addUserNickName(@Body userNickName: String): ApiResponse<Map<String, String>>

    @POST("users/{uid}.json")
    suspend fun addUser(@Path("uid") uid: String, @Body user: User): ApiResponse<Map<String, String>>

    @POST("posts/{postUid}.json")
    suspend fun addPost(@Path("postUid") postUid: String, @Body post: Post): ApiResponse<Map<String, String>>

    @GET("posts.json")
    suspend fun getAllPosts(): ApiResponse<Map<String, Map<String, Post>>>

    @GET("users.json")
    suspend fun getAllUsers(): ApiResponse<Map<String, Map<String, User>>>

    @PUT("posts/{postUid}/{firebaseUid}.json")
    suspend fun updatePost(@Path("postUid") postUid: String, @Path("firebaseUid") firebaseUid: String, @Body post: Post): ApiResponse<Post>

    @PUT("users/{uid}/{firebaseUid}.json")
    suspend fun updateUser(@Path("uid") uid: String, @Path("firebaseUid") firebaseUid: String, @Body user: User): ApiResponse<User>

    @GET("posts/{postUid}/{firebaseUid}.json")
    suspend fun getPost(@Path("postUid") postUid: String, @Path("firebaseUid") firebaseUid: String): ApiResponse<Post>

    @GET("posts/{postUid}.json")
    suspend fun getPostNoFirebaseUid(@Path("postUid") postUid: String): ApiResponse<Map<String, Post>>

    @POST("chat/{postUid}.json")
    suspend fun addChat(@Path("postUid") postUid: String, @Body message: Message): ApiResponse<Map<String, String>>

    @GET("chat/{postUid}.json")
    suspend fun getAllChat(@Path("postUid") postUid: String): ApiResponse<Map<String, Message>>

    @GET("users/{userUid}/{firebaseUid}.json")
    suspend fun getUser(@Path("userUid") userUid: String, @Path("firebaseUid") firebaseUid: String): ApiResponse<User>

    @GET("users/{userUid}.json")
    suspend fun getUserNoFirebaseUid(@Path("userUid") userUid: String): ApiResponse<Map<String, User>>

    @PUT("latestChat/{postUid}.json")
    suspend fun addLatestChat(@Path("postUid") postUid: String, @Body chatRoomInfo: ChatRoomInfo): ApiResponse<ChatRoomInfo>

    @GET("latestChat/{postUid}.json")
    suspend fun getLatestChat(@Path("postUid") postUid: String): ApiResponse<ChatRoomInfo>
}