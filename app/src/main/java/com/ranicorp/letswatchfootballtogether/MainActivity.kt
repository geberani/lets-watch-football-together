package com.ranicorp.letswatchfootballtogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.ui.home.HomeFragment

class MainActivity : AppCompatActivity() {

    private val userPreferenceRepository = UserPreferenceRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        moveToFirstScreen()
    }

    private fun moveToFirstScreen() {
        if (userPreferenceRepository.getFirebaseUserId().isNotEmpty()) {
            supportFragmentManager.commit {
                replace(R.id.homeNavHost, HomeFragment())
            }
        }
    }
}