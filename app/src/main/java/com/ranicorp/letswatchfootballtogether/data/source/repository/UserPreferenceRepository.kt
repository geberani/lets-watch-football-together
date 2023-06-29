package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.ranicorp.letswatchfootballtogether.FootballApplication
import com.ranicorp.letswatchfootballtogether.util.Constants

class UserPreferenceRepository {

    fun saveUserInfo(googleUid: String) {
        with(FootballApplication.preferencesManager) {
            putString(Constants.KEY_FIREBASE_UID, googleUid)
        }
    }

    fun getUserUid(): String {
        return with(FootballApplication.preferencesManager) {
            getString(Constants.KEY_FIREBASE_UID, "")
        }
    }

    fun saveUserNickName(nickName: String) {
        with(FootballApplication.preferencesManager) {
            putString(Constants.KEY_FIREBASE_NICK_NAME, nickName)
        }
    }

    fun getUserNickName(): String {
        return with(FootballApplication.preferencesManager) {
            getString(Constants.KEY_FIREBASE_NICK_NAME, "")
        }
    }
}