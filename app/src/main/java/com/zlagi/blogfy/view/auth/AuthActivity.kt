package com.zlagi.blogfy.view.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.zlagi.blogfy.R
import com.zlagi.blogfy.databinding.ActivityAuthBinding
import com.zlagi.blogfy.view.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_AuthBlogfy)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupNavigationController()
        setupActionBar()
    }

    private fun setupNavigationController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.auth_nav_host_container) as NavHostFragment
        navController = navHostFragment.navController
    }

    fun navMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    private fun setupActionBar() {
        supportActionBar?.hide()
    }
}
