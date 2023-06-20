package com.ranicorp.letswatchfootballtogether.ui.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.firebase.auth.FirebaseUser
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository

class SignInViewModel(private val userPreferenceRepository: UserPreferenceRepository) : ViewModel() {

    fun saveUserInfo(user: FirebaseUser) {
        userPreferenceRepository.saveUserInfo(user)
    }

    companion object {
        fun provideFactory(userPreferenceRepository: UserPreferenceRepository) = viewModelFactory {
     initializer {
            SignInViewModel(userPreferenceRepository)
        }
    }
    }
}
