package com.zlagi.blogfy.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zlagi.blogfy.R.id.*
import com.zlagi.blogfy.databinding.ActivityMainBinding
import com.zlagi.blogfy.view.auth.AuthActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var navController: NavController

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hideBottomNavigation()
        setupNavigationController()
        setupBottomNavigationView()
        setupActionBar()
    }

    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(main_nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    private fun setupBottomNavigationView() {
        bottomNavigationView = findViewById(bottom_nav)
        bottomNavigationView.setupWithNavController(navController)
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }

    private fun hideBottomNavigation() {
        // Hide bottom nav on screens which don't require it
        lifecycleScope.launchWhenResumed {
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    onBoardingFragment,
                    signInFragment,
                    createBlogFragment,
                    updateBlogFragment,
                    updatePasswordFragment -> binding.bottomNav.visibility =
                        View.GONE
                    else -> binding.bottomNav.visibility = View.VISIBLE
                }
            }
        }
    }

    fun navAuthActivity() {
        val intent = Intent(this, AuthActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}
