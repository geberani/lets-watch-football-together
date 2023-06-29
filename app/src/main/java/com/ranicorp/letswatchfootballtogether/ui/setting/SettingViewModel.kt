package com.ranicorp.letswatchfootballtogether.ui.setting

import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.util.Constants
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class SettingViewModel(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    val profileUri: MutableLiveData<String> = MutableLiveData()
    val nickName: MutableLiveData<String> = MutableLiveData()
    private val _errorType: MutableLiveData<String?> = MutableLiveData()
    val errorMsg: LiveData<String?> = _errorType
    private val isValidProfileUri: MutableLiveData<Boolean> = MutableLiveData()
    private val isValidNickName: MutableLiveData<Boolean> = MutableLiveData()
    private val existingNickName: MutableLiveData<Collection<String>> = MutableLiveData()

    fun setProfileUri(uri: String) {
        profileUri.value = uri
        isValidProfileUri.value = !profileUri.value.isNullOrEmpty()
    }

    fun setNickName(nickName: String) {
        this.nickName.value = nickName
    }

    fun validateNickName(nickName: String) {
        if (this.nickName.value.isNullOrBlank()) {
            _errorType.value = Constants.ERROR_EMPTY_NICK_NAME
        } else if (this.nickName.value!!.length > 20) {
            _errorType.value = Constants.ERROR_MAX_LENGTH
        } else {
            viewModelScope.launch {
                verifyDuplicateNickName(nickName)
            }
        }
    }

    private fun verifyDuplicateNickName(nickName: String) {
        if (existingNickName.value?.contains(nickName) == false) {
            isValidNickName.value = true
            _errorType.value = null
        } else {
            isValidNickName.value = false
            _errorType.value = Constants.ERROR_DUPLICATE_NICK_NAME
        }
    }

    fun updateExistingNickNameList() {
        viewModelScope.launch {
            existingNickName.value =
                userRepository.getUserNickNames().body()?.values ?: emptyList()
        }
    }

    fun addUser() {
        if (profileUri.value.isNullOrEmpty() || nickName.value.isNullOrEmpty()) {
            //TODO 프로필 또는 닉네임을 설정하지 않았을 때에 대한 처리
            return
        }

        viewModelScope.launch {
            val imageLocations = addImageToStorage(profileUri.value!!)
            val user =
                User(
                    userPreferenceRepository.getUserUid(),
                    nickName.value!!,
                    imageLocations,
                    emptyList()
                )
            userRepository.addUser(user)
            userRepository.addUserNickName(nickName.value!!)
        }
        userPreferenceRepository.saveUserNickName(nickName.value!!)
    }

    private suspend fun addImageToStorage(profileUri: String): String = coroutineScope {
        val location = "images/${profileUri}"
        val imageRef = firebaseStorage.getReference(location)
        imageRef.putFile(profileUri.toUri()).await()
        location
    }

    companion object {
        fun provideFactory(
            userPreferenceRepository: UserPreferenceRepository,
            userRepository: UserRepository,
            firebaseStorage: FirebaseStorage
        ) = viewModelFactory {
            initializer {
                SettingViewModel(userPreferenceRepository, userRepository, firebaseStorage)
            }
        }
    }
}