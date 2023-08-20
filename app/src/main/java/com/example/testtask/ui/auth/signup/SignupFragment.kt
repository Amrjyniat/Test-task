package com.example.testtask.ui.auth.signup

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
import com.example.testtask.databinding.FragmentSignupBinding
import com.example.testtask.onError
import com.example.testtask.onSuccess
import com.example.testtask.ui.home.HomeActivity
import com.example.testtask.utils.bind
import com.example.testtask.utils.bindEnable
import com.example.testtask.utils.launchAndRepeatInLifecycle
import com.example.testtask.utils.showErrorDialog

class SignupFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private val viewModel: SignupViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)

        binding.apply {
            edEmail.bind(viewLifecycleOwner, viewModel.emailInput)
            edPassword.bind(viewLifecycleOwner, viewModel.passInput)
            btnSignup.bindEnable(viewLifecycleOwner, viewModel.isSubmittable)

            btnSignup.setOnClickListener { viewModel.signup() }
            tvSignIn.setOnClickListener { navigateToLoginScreen() }
        }

        viewModel.signupResult.launchAndRepeatInLifecycle(viewLifecycleOwner) { result ->
            binding.progressLoading.isVisible = result is ApiLoading

            result.onSuccess {
                navigateToWelcomeScreen()
            }.onError {
                requireActivity().showErrorDialog(it)
            }
        }


        return binding.root
    }

    private fun navigateToLoginScreen() {
        findNavController().navigate(SignupFragmentDirections.goToLogin())
    }

    private fun navigateToWelcomeScreen() {
        val intent = Intent(context, HomeActivity::class.java).apply {
            putExtra(HomeActivity.isFirstTimeSigninArg, true)
        }
        startActivity(intent)
        requireActivity().finish()
    }

}