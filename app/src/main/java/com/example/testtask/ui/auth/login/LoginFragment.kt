package com.example.testtask.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.testtask.ApiLoading
import com.example.testtask.SplashActivity
import com.example.testtask.databinding.FragmentLoginBinding
import com.example.testtask.onError
import com.example.testtask.onSuccess
import com.example.testtask.ui.home.HomeActivity
import com.example.testtask.ui.home.HomeActivity.Companion.isFirstTimeSigninArg
import com.example.testtask.utils.bind
import com.example.testtask.utils.bindEnable
import com.example.testtask.utils.launchAndRepeatInLifecycle
import com.example.testtask.utils.showErrorDialog

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        binding.apply {
            edEmail.bind(viewLifecycleOwner, viewModel.emailInput)
            edPassword.bind(viewLifecycleOwner, viewModel.passInput)
            btnSignin.bindEnable(viewLifecycleOwner, viewModel.isSubmittable)

            btnSignin.setOnClickListener { viewModel.signin() }
            tvSignUp.setOnClickListener { navigateToSignupScreen() }
        }

        viewModel.signinResult.launchAndRepeatInLifecycle(viewLifecycleOwner) { result ->
            binding.progressLoading.isVisible = result is ApiLoading
            result.onSuccess {
                navigateToWelcomeScreen()
            }.onError {
                requireActivity().showErrorDialog(it)
            }
        }


        return binding.root
    }

    private fun navigateToSignupScreen() {
        findNavController().navigate(LoginFragmentDirections.goToSignup())
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra(isFirstTimeSigninArg, true)
        }
        startActivity(intent)
        requireActivity().finish()
    }

}