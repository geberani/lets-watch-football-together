package com.ranicorp.letswatchfootballtogether

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.ranicorp.letswatchfootballtogether.data.source.repository.UserPreferenceRepository
import com.ranicorp.letswatchfootballtogether.databinding.ActivityMainBinding
import com.ranicorp.letswatchfootballtogether.ui.home.HomeFragmentDirections
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
        setNavigation()
    }

    private fun setNavigation() {
        val navController =
            supportFragmentManager.findFragmentById(R.id.homeNavHost)?.findNavController()
        navController?.let {
            binding.homeBottomNavigationView.setupWithNavController(it)
        }

        if (userPreferenceRepository.getUserUid().isEmpty()) {
            val action = HomeFragmentDirections.actionHomeFragmentToSignInFragment()
            navController?.navigate(action)
        }

        navController?.addOnDestinationChangedListener { _, destination, _ ->
            val bottomNavigation = binding.homeBottomNavigationView
            when (destination.id) {
                R.id.homeFragment, R.id.profileFragment, R.id.chatRoomListFragment -> {
                    bottomNavigation.isVisible = true
                }
                else -> {
                    bottomNavigation.isVisible = false
                }
            }
        }
    }
}