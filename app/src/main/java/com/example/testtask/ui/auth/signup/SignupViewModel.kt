package com.example.testtask.ui.auth.signup

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

class SignupViewModel : ViewModel() {

    private val auth = Firebase.auth
    private val signupChannel = Channel<ApiResult<FirebaseUser?>>()
    val signupResult = signupChannel.receiveAsFlow()
        .shareIn(viewModelScope, SharingStarted.Lazily)


    val emailInput = MutableStateFlow("")
    val passInput = MutableStateFlow("")

    val isSubmittable = combine(emailInput, passInput) { email, pass ->
        email.isValidAndNotEmptyEmail() && pass.length >= 6
    }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    fun signup() {
        signupChannel.trySend(ApiLoading())
        auth.createUserWithEmailAndPassword(emailInput.value, passInput.value)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    signupChannel.trySend(ApiSuccess(user))
                } else {
                    // If sign in fails, display a message to the user.
                    signupChannel.trySend(ApiError(message = task.exception?.localizedMessage))
                }
            }
    }


}