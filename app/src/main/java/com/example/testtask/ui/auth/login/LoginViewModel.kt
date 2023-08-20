package com.example.testtask.ui.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testtask.ApiError
import com.example.testtask.ApiLoading
import com.example.testtask.ApiResult
import com.example.testtask.ApiSuccess
import com.example.testtask.utils.isValidAndNotEmptyEmail
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn

class LoginViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val signinChannel = Channel<ApiResult<FirebaseUser?>>()
    val signinResult = signinChannel.receiveAsFlow()
        .shareIn(viewModelScope, SharingStarted.Lazily)


    val emailInput = MutableStateFlow("")
    val passInput = MutableStateFlow("")

    val isSubmittable = combine(emailInput, passInput) { email, pass ->
        email.isValidAndNotEmptyEmail() && pass.length >= 6
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun signin() {
        signinChannel.trySend(ApiLoading())
        auth.signInWithEmailAndPassword(emailInput.value, passInput.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    signinChannel.trySend(ApiSuccess(user))
                } else {
                    signinChannel.trySend(ApiError(message = task.exception?.localizedMessage))
                }
            }
    }


}