package com.example.testtask

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testtask.databinding.ActivitySplashBinding
import com.example.testtask.ui.auth.AuthActivity
import com.example.testtask.ui.home.HomeActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val currUser = Firebase.auth.currentUser

        if (currUser != null) {
            startActivity(Intent(this, HomeActivity::class.java))
        } else {
            startActivity(Intent(this, AuthActivity::class.java))
        }

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}