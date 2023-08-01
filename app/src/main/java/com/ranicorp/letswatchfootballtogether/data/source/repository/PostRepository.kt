package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.source.remote.RemoteDataSource
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.remote.onError
import com.ranicorp.letswatchfootballtogether.data.source.remote.onException
import com.ranicorp.letswatchfootballtogether.data.source.remote.onSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import javax.inject.Inject

class PostRepository @Inject constructor(private val remoteDataSource: RemoteDataSource) {

    fun addPost(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String,
        post: Post
    ): Flow<ApiResponse<Map<String, String>>> = flow {
        val response = remoteDataSource.addPost(postUid, post)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getAllPosts(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit
    ): Flow<List<Post>> = flow {
        val response = remoteDataSource.getAllPosts()
        response.onSuccess { data ->
            val posts = data?.values?.flatMap { it.values } ?: emptyList()
            emit(posts)
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun updatePost(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String,
        firebaseUid: String,
        post: Post
    ): Flow<ApiResponse<Post>> = flow {
        val response = remoteDataSource.updatePost(postUid, firebaseUid, post)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getPost(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String,
        firebaseUid: String
    ): Flow<ApiResponse<Post>> = flow {
        val response = remoteDataSource.getPost(postUid, firebaseUid)
        response.onSuccess { data ->
            emit(ApiResultSuccess(data))
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)

    fun getPostNoFirebaseUid(
        onComplete: () -> Unit,
        onError: (message: String?) -> Unit,
        postUid: String
    ): Flow<Map<String, Post>> = flow {
        val response = remoteDataSource.getPostNoFirebaseUid(postUid)
        response.onSuccess { data ->
            emit(data ?: mapOf<String, Post>())
        }.onError { code, message ->
            onError("code: $code, message: $message")
        }.onException {
            onError(it.message)
        }
    }.onCompletion {
        onComplete()
    }.flowOn(Dispatchers.Default)
}