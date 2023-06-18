package com.ranicorp.letswatchfootballtogether.data.source.repository

import com.google.firebase.auth.FirebaseUser
import com.ranicorp.letswatchfootballtogether.FootballApplication
import com.ranicorp.letswatchfootballtogether.util.Constants

class UserPreferenceRepository {

    fun saveUserInfo(user: FirebaseUser) {
        with(FootballApplication.preferencesManager) {
            putString(Constants.KEY_FIREBASE_UID, user.uid)
        }
    }

    fun getFirebaseUserId(): String {
        return with(FootballApplication.preferencesManager) {
            getString(Constants.KEY_FIREBASE_UID, "")
        }
    }
}