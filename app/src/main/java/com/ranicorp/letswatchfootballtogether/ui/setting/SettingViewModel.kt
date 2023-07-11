package com.ranicorp.letswatchfootballtogether.ui.setting

import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.ui.common.Event
import com.ranicorp.letswatchfootballtogether.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    val profileUri: MutableLiveData<String> = MutableLiveData()
    val nickName: MutableLiveData<String> = MutableLiveData()
    private val _errorType: MutableLiveData<Event<String?>> = MutableLiveData()
    val errorMsg: LiveData<Event<String?>> = _errorType
    private val isValidProfileUri: MutableLiveData<Boolean> = MutableLiveData()
    private val isValidNickName: MutableLiveData<Boolean> = MutableLiveData()
    private val existingNickName: MutableLiveData<List<String>> = MutableLiveData()
    private val _isSettingComplete = MutableLiveData(Event(false))
    val isSettingComplete: LiveData<Event<Boolean>> = _isSettingComplete
    private val isAddUserComplete = MutableLiveData(false)
    private val isAddNickNameComplete = MutableLiveData(false)
    val hasAllNickName: MutableLiveData<Boolean> = MutableLiveData()

    fun setProfileUri(uri: String) {
        profileUri.value = uri
        isValidProfileUri.value = !profileUri.value.isNullOrEmpty()
    }

    fun setNickName(nickName: String) {
        this.nickName.value = nickName
    }

    fun validateNickName(nickName: String) {
        if (this.nickName.value.isNullOrBlank()) {
            _errorType.value = Event(Constants.ERROR_EMPTY_NICK_NAME)
        } else if (this.nickName.value!!.length > 20) {
            _errorType.value = Event(Constants.ERROR_MAX_LENGTH)
        } else {
            viewModelScope.launch {
                verifyDuplicateNickName(nickName)
            }
        }
    }

    private fun verifyDuplicateNickName(nickName: String) {
        if (hasAllNickName.value == false) return
        if (existingNickName.value?.contains(nickName) == false) {
            isValidNickName.value = true
            _errorType.value = Event(null)
        } else {
            isValidNickName.value = false
            _errorType.value = Event(Constants.ERROR_DUPLICATE_NICK_NAME)
        }
    }

    fun updateExistingNickNameList() {
        viewModelScope.launch {
            val networkResult = userRepository.getUserNickNames()
            when (networkResult) {
                is ApiResultSuccess -> {
                    existingNickName.value = networkResult.data.values.toList()
                    hasAllNickName.value = true
                }
                is ApiResultError -> {
                    hasAllNickName.value = false
                    Log.d(
                        "SettingViewModel",
                        "Error code: ${networkResult.code}, message: ${networkResult.message}"
                    )
                }
                is ApiResultException -> {
                    hasAllNickName.value = false
                    Log.d("SettingViewModel", "Exception: ${networkResult.throwable}")
                }
            }
        }
    }

    fun addUser(googleUid: String) {
        if (profileUri.value.isNullOrEmpty() || nickName.value.isNullOrEmpty()) {
            return
        }

        viewModelScope.launch {
            val imageLocations = addImageToStorage(profileUri.value!!)
            val user =
                User(
                    googleUid,
                    nickName.value!!,
                    imageLocations,
                    mutableListOf()
                )

            addUserCall(googleUid, user)
            addNickNameCall()
            if (isAddNickNameComplete.value == true && isAddUserComplete.value == true) {
                _isSettingComplete.value = Event(true)
                userPreferenceRepository.saveUserInfo(googleUid)
                userPreferenceRepository.saveUserNickName(nickName.value!!)
            } else {
                _isSettingComplete.value = Event(false)
            }
        }
    }

    private suspend fun addNickNameCall() {
        val addUserNickNameResult = userRepository.addUserNickName(nickName.value!!)
        when (addUserNickNameResult) {
            is ApiResultSuccess -> {
                isAddNickNameComplete.value = true
            }
            is ApiResultError -> {
                isAddNickNameComplete.value = false
                Log.d(
                    "SettingViewModel",
                    "Error code: ${addUserNickNameResult.code}, message: ${addUserNickNameResult.message}"
                )
            }
            is ApiResultException -> {
                isAddNickNameComplete.value = false
                Log.d("SettingViewModel", "Exception: ${addUserNickNameResult.throwable}")
            }
        }
    }

    private suspend fun addUserCall(googleUid: String, user: User) {
        val addUserResult = userRepository.addUser(
            googleUid,
            user
        )
        when (addUserResult) {
            is ApiResultSuccess -> {
                isAddUserComplete.value = true
            }
            is ApiResultError -> {
                isAddUserComplete.value = false
                Log.d(
                    "SettingViewModel",
                    "Error code: ${addUserResult.code}, message: ${addUserResult.message}"
                )
            }
            is ApiResultException -> {
                isAddUserComplete.value = false
                Log.d("SettingViewModel", "Exception: ${addUserResult.throwable}")
            }
        }
    }

    private suspend fun addImageToStorage(profileUri: String): String = coroutineScope {
        val location = "images/${profileUri}"
        val imageRef = firebaseStorage.getReference(location)
        imageRef.putFile(profileUri.toUri()).await()
        location
    }
}