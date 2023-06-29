package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.ranicorp.letswatchfootballtogether.BuildConfig
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
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

    companion object {

        private const val BASE_URL = BuildConfig.BASE_URL
        private val moshi = Moshi.Builder().build()

        fun create(): ApiClient {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val header = Interceptor { chain ->
                val newRequest = chain.request().newBuilder()
                    .build()
                chain.proceed(newRequest)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .addInterceptor(header)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(ApiClient::class.java)
        }
    }
}