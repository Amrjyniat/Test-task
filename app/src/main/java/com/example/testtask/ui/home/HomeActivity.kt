package com.example.testtask.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.example.testtask.R
import com.example.testtask.databinding.ActivityHomeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController: NavController
    private val isFirstTimeSignin: Boolean by lazy {
        intent.getBooleanExtra(isFirstTimeSigninArg, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_home_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (!isFirstTimeSignin){
            navController.popBackStack(R.id.welcomeFragment, true)
            navController.navigate(R.id.listFragment)
        }
    }

    companion object {
        const val isFirstTimeSigninArg = "isFirstTimeSigninArg"
    }

}