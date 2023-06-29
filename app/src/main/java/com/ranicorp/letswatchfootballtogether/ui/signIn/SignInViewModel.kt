package com.ranicorp.letswatchfootballtogether.ui.signIn

import androidx.lifecycle.ViewModel
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(private val userPreferenceRepository: UserPreferenceRepository) : ViewModel() {

    fun saveUserInfo(googleUid: String) {
        userPreferenceRepository.saveUserInfo(googleUid)
    }
}
