package com.ranicorp.letswatchfootballtogether.ui.setting

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.FirebaseStorage
import com.ranicorp.letswatchfootballtogether.data.model.User
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import com.ranicorp.letswatchfootballtogether.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val userPreferenceRepository: UserPreferenceRepository,
    private val userRepository: UserRepository,
    private val firebaseStorage: FirebaseStorage
) : ViewModel() {

    val profileUri: MutableStateFlow<String> = MutableStateFlow("")
    val nickName: MutableStateFlow<String> = MutableStateFlow("")
    private val _nickNameErrorType: MutableStateFlow<String?> = MutableStateFlow(null)
    val nickNameErrorType: StateFlow<String?> = _nickNameErrorType
    private val _settingErrorType: MutableStateFlow<String?> = MutableStateFlow(null)
    val settingErrorType: StateFlow<String?> = _settingErrorType
    private val isValidProfileUri: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isValidNickName: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val _existingNickName = MutableStateFlow<List<String>>(emptyList())
    val existingNickName: StateFlow<List<String>> = _existingNickName
    private val _isSettingComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val isSettingComplete: StateFlow<Boolean> = _isSettingComplete
    private val isInputComplete: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private val isAddUserComplete = MutableStateFlow(false)
    private val isAddNickNameComplete = MutableStateFlow(false)
    private val _hasAllNickName: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasAllNickName: StateFlow<Boolean> = _hasAllNickName

    fun setProfileUri(uri: String) {
        profileUri.value = uri
        isValidProfileUri.value = profileUri.value.isNotEmpty()
    }

    fun setNickName(nickName: String) {
        this.nickName.value = nickName
    }

    fun validateNickName(nickName: String) {
        if (this.nickName.value.isBlank()) {
            _nickNameErrorType.value = Constants.ERROR_EMPTY_NICK_NAME
            isValidNickName.value = false
        } else if (this.nickName.value.length > 20) {
            _nickNameErrorType.value = Constants.ERROR_MAX_LENGTH
            isValidNickName.value = false
        } else {
            viewModelScope.launch {
                verifyDuplicateNickName(nickName)
            }
        }
    }

    private fun verifyDuplicateNickName(nickName: String) {
        if (hasAllNickName.value == false) return
        if (!existingNickName.value.contains(nickName)) {
            isValidNickName.value = true
            _nickNameErrorType.value = null
        } else {
            isValidNickName.value = false
            _nickNameErrorType.value = Constants.ERROR_DUPLICATE_NICK_NAME
        }
    }

    fun updateExistingNickNameList() {
        viewModelScope.launch {
            userRepository.getUserNickNames(
                onComplete = { _hasAllNickName.value = true },
                onError = {
                    _hasAllNickName.value = false
                    _settingErrorType.value = Constants.ERROR_SETTING_NOT_AVAILABLE
                }
            ).map { data ->
                data
            }.collect { data ->
                _existingNickName.value = data
            }
        }
    }

    fun addUser(googleUid: String) {
        val validInput = isValidProfileUri.value && isValidNickName.value
        if (!validInput) {
            isInputComplete.value = false
            _settingErrorType.value = Constants.ERROR_SETTING_INPUT_NOT_COMPLETE
            return
        }

        viewModelScope.launch {
            isInputComplete.value = true
            val imageLocations = addImageToStorage(profileUri.value)
            val user =
                User(
                    googleUid,
                    nickName.value,
                    imageLocations,
                    mutableListOf()
                )

            async { addNickNameCall() }.await()
            async { addUserCall(googleUid, user) }.await()

            if (isAddNickNameComplete.value && isAddUserComplete.value) {
                _isSettingComplete.value = true
                userPreferenceRepository.saveUserInfo(googleUid)
                userPreferenceRepository.saveUserNickName(nickName.value)
            } else {
                _isSettingComplete.value = false
                _settingErrorType.value = Constants.ERROR_SETTING_FAILED
            }
        }
    }

    private suspend fun addNickNameCall() {
        userRepository.addUserNickName(
            onComplete = { isAddNickNameComplete.value = true },
            onError = { isAddNickNameComplete.value = false },
            nickName.value
        ).collect {
            isAddNickNameComplete.value = true
        }
    }

    private suspend fun addUserCall(googleUid: String, user: User) {
        userRepository.addUser(
            onComplete = { isAddUserComplete.value = true },
            onError = { isAddUserComplete.value = false },
            googleUid,
            user
        ).collect {
            isAddUserComplete.value = true
        }
    }

    private suspend fun addImageToStorage(profileUri: String): String = coroutineScope {
        val location = "images/${profileUri}"
        val imageRef = firebaseStorage.getReference(location)
        imageRef.putFile(profileUri.toUri()).await()
        location
    }
}