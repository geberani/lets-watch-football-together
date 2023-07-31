package com.ranicorp.letswatchfootballtogether.ui.signIn

import androidx.lifecycle.ViewModel
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _hasAllUsers: MutableStateFlow<Boolean?> = MutableStateFlow(null)
    val hasAllUsers: StateFlow<Boolean?> = _hasAllUsers
    private val _hasJoined: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val hasJoined: StateFlow<Boolean> = _hasJoined
    private val hasSavedUserInfo: MutableStateFlow<Boolean>? = null
    private var userNickname = ""

    fun confirmHasJoined(userFirebaseUid: String) = userRepository.getAllUsers(
        onComplete = { _hasAllUsers.value = true },
        onError = { _hasAllUsers.value = false }
    ).map { data ->
        val allUsersList = data.values.flatMap { it.values }
        val allUsersFirebaseUidList = data.keys.toList()
        _hasAllUsers.value = true
        _hasJoined.value = allUsersFirebaseUidList.contains(userFirebaseUid)
        if (hasJoined.value) {
            val userInfo = allUsersList.filter {
                it.uid == userFirebaseUid
            }.first()
            userPreferenceRepository.saveUserInfo(userFirebaseUid)
            userPreferenceRepository.saveUserNickName(userInfo.nickName)
        }
    }

    fun saveUserInfo(googleUid: String) {
        userPreferenceRepository.saveUserInfo(googleUid)
        getUserNickName(googleUid)
        userPreferenceRepository.saveUserNickName(userNickname)
    }

    private fun getUserNickName(googleUid: String) = userRepository.getUserNoFirebaseUid(
        onComplete = { hasSavedUserInfo?.value = true },
        onError = { hasSavedUserInfo?.value = false },
        googleUid
    ).map { data ->
        userNickname = data.values.first().nickName
        hasSavedUserInfo?.value = true
    }
}

