package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.data.source.PreferenceManager
import com.ranicorp.letswatchfootballtogether.util.Constants
import javax.inject.Inject

class UserPreferenceRepository @Inject constructor(private val preferencesManager: PreferenceManager) {

    fun saveUserInfo(googleUid: String) {
        with(preferencesManager) {
            putString(Constants.KEY_FIREBASE_UID, googleUid)
        }
    }

    fun getUserUid(): String {
        return with(preferencesManager) {
            getString(Constants.KEY_FIREBASE_UID, "")
        }
    }

    fun saveUserNickName(nickName: String) {
        with(preferencesManager) {
            putString(Constants.KEY_FIREBASE_NICK_NAME, nickName)
        }
    }

    fun getUserNickName(): String {
        return with(preferencesManager) {
            getString(Constants.KEY_FIREBASE_NICK_NAME, "")
        }
    }

    fun deleteUser() {
        with(preferencesManager) {
            deleteUser()
        }
    }
}