package com.ranicorp.letswatchfootballtogether.data.source.remote

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.ranicorp.letswatchfootballtogether.data.model.ChatRoomInfo
import com.ranicorp.letswatchfootballtogether.data.model.Message
import com.ranicorp.letswatchfootballtogether.data.model.Post
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResponse
import javax.inject.Inject

class RemoteDataSource @Inject constructor(private val apiClient: ApiClient) {

    suspend fun getUserNickNames(): ApiResponse<Map<String, String>> {
        return apiClient.getUserNickNames()
    }

    suspend fun addUserNickName(userNickName: String): ApiResponse<Map<String, String>> {
        return apiClient.addUserNickName(userNickName)
    }

    suspend fun addUser(uid: String, user: User): ApiResponse<Map<String, String>> {
        return apiClient.addUser(uid, user)
    }

    suspend fun addPost(postUid: String, post: Post): ApiResponse<Map<String, String>> {
        return apiClient.addPost(postUid, post)
    }

    suspend fun getAllPosts(): ApiResponse<Map<String, Map<String, Post>>> {
        return apiClient.getAllPosts()
    }

    suspend fun getAllUsers(): ApiResponse<Map<String, Map<String, User>>> {
        return apiClient.getAllUsers()
    }

    suspend fun updatePost(postUid: String, firebaseUid: String, post: Post): ApiResponse<Post> {
        return apiClient.updatePost(postUid, firebaseUid, post)
    }

    suspend fun updateUser(uid: String, firebaseUid: String, user: User): ApiResponse<User> {
        return apiClient.updateUser(uid, firebaseUid, user)
    }

    suspend fun getPost(postUid: String, firebaseUid: String): ApiResponse<Post> {
        return apiClient.getPost(postUid, firebaseUid)
    }

    suspend fun getPostNoFirebaseUid(postUid: String): ApiResponse<Map<String, Post>> {
        return apiClient.getPostNoFirebaseUid(postUid)
    }

    suspend fun addChat(
        postUid: String,
        message: Message
    ): ApiResponse<Map<String, String>> {
        return apiClient.addChat(postUid, message)
    }

    suspend fun getAllChat(postUid: String): ApiResponse<Map<String, Message>> {
        return apiClient.getAllChat(postUid)
    }

    suspend fun getUser(userUid: String, firebaseUid: String): ApiResponse<User> {
        return apiClient.getUser(userUid, firebaseUid)
    }

    suspend fun getUserNoFirebaseUid(userUid: String): ApiResponse<Map<String, User>> {
        return apiClient.getUserNoFirebaseUid(userUid)
    }

    suspend fun addLatestChat(postUid: String, chatRoomInfo: ChatRoomInfo): ApiResponse<ChatRoomInfo> {
        return apiClient.addLatestChat(postUid, chatRoomInfo)
    }

    suspend fun getLatestChat(postUid: String): ApiResponse<ChatRoomInfo> {
        return apiClient.getLatestChat(postUid)
    }

    suspend fun deleteUserNoFirebaseUid(userUid: String): ApiResponse<Map<String, User>> {
        return apiClient.deleteUserNoFirebaseUid(userUid)
    }

    fun addChatEventListener(
        chatRoomUid: String,
        onChatItemAdded: (Message) -> Unit
    ): ChildEventListener {
        val chatEventListener = object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(Message::class.java)
                chatItem?.let { onChatItemAdded(it) }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        }
        val database = Firebase.database(
            com.ranicorp.letswatchfootballtogether.BuildConfig.BASE_URL
        ).reference
        database.child("chat").child(chatRoomUid).addChildEventListener(chatEventListener)
        return chatEventListener
    }
}