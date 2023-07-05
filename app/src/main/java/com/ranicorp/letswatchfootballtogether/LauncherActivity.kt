package com.ranicorp.letswatchfootballtogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.databinding.ActivityMainBinding
import com.ranicorp.letswatchfootballtogether.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LauncherActivity : AppCompatActivity() {

    @Inject
    lateinit var userPreferenceRepository: UserPreferenceRepository
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        moveToFirstScreen()
        setBottomNavigation()
    }

    private fun setBottomNavigation() {
        val navController = supportFragmentManager.findFragmentById(R.id.homeNavHost)?.findNavController()
        navController?.let {
            binding.homeBottomNavigationView.setupWithNavController(it)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNavigation = binding.homeBottomNavigationView
            when (destination.id) {
                R.id.homeFragment, R.id.profileFragment -> {
                    bottomNavigation.isVisible = true
                }
                else -> {
                    bottomNavigation.isVisible = false
                }
            }
        }
    }

    private fun moveToFirstScreen() {
        if (userPreferenceRepository.getUserUid().isNotEmpty()) {
            supportFragmentManager.commit {
                replace(R.id.homeNavHost, HomeFragment())
            }
        }
    }
}