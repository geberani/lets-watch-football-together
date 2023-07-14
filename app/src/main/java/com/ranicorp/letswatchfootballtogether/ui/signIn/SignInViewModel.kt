package com.ranicorp.letswatchfootballtogether.ui.signIn

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultError
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultException
import com.ranicorp.letswatchfootballtogether.data.source.remote.apicalladapter.ApiResultSuccess
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val userPreferenceRepository: UserPreferenceRepository
) : ViewModel() {

    private val _hasAllUsers: MutableLiveData<Boolean> = MutableLiveData()
    val hasAllUsers: LiveData<Boolean> = _hasAllUsers
    private val _hasJoined: MutableLiveData<Boolean> = MutableLiveData()
    val hasJoined: LiveData<Boolean> = _hasJoined

    fun confirmExistingUid(userFirebaseUid: String) {
        viewModelScope.launch {
            val networkResult = userRepository.getAllUsers()
            when (networkResult) {
                is ApiResultSuccess -> {
                    val allUsersList = networkResult.data?.values?.flatMap { it.values }
                    val allUsersFirebaseUidList = networkResult.data?.keys?.toList()
                    _hasAllUsers.value = true
                    _hasJoined.value = allUsersFirebaseUidList?.contains(userFirebaseUid)
                    if (_hasJoined.value == true) {
                        val userInfo = allUsersList?.filter {
                            it.uid == userFirebaseUid
                        }?.first()
                        userPreferenceRepository.saveUserInfo(userFirebaseUid)
                        userPreferenceRepository.saveUserNickName(userInfo?.nickName ?: "")
                    }
                }
                is ApiResultError -> {
                    _hasAllUsers.value = false
                    Log.d(
                        "SignInViewModel",
                        "Error code: ${networkResult.code}, message: ${networkResult.message}"
                    )
                }
                is ApiResultException -> {
                    _hasAllUsers.value = false
                    Log.d("SignInViewModel", "Exception: ${networkResult.throwable}")
                }
            }
        }
    }
}

