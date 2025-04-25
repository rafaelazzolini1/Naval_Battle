package com.example.navalbattle.ui.theme.screen.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naval.battle.data.repository.AuthRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {
    val email = mutableStateOf("")
    val password = mutableStateOf("")
    val emailError = mutableStateOf(false)
    val passwordError = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun signIn(onSuccess: () -> Unit) {
        emailError.value = email.value.isEmpty()
        passwordError.value = password.value.isEmpty()

        if (!emailError.value && !passwordError.value) {
            viewModelScope.launch {
                val result = authRepository.signIn(email.value, password.value)
                if (result.isSuccess) {
                    clearUserData()
                    onSuccess()
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Login failed"
                }
            }
        } else {
            errorMessage.value = "Please fill in all fields!"
        }
    }

    fun registerWithEmailVerification(onComplete: (Boolean) -> Unit) {
        emailError.value = email.value.isEmpty()
        passwordError.value = password.value.isEmpty()

        if (!emailError.value && !passwordError.value) {
            viewModelScope.launch {
                val result = authRepository.register(email.value, password.value)
                if (result.isSuccess) {
                    val verificationResult = authRepository.sendEmailVerification()
                    if (verificationResult.isSuccess) {
                        errorMessage.value = "Registration successful! A verification email has been sent"
                    } else {
                        errorMessage.value = "Account created but failed to send verification email"
                        onComplete(false)
                    }
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
                    onComplete(false)
                }
            }
        } else {
            errorMessage.value = "Please fill in all fields!"
            onComplete(false)
        }
        clearUserData()
    }

    fun resetPassword(resetEmail: String, onComplete: (Boolean) -> Unit) {
        if (resetEmail.isEmpty()) {
            errorMessage.value = "Please enter an email address"
            onComplete(false)
            return
        }

        viewModelScope.launch {
            val result = authRepository.resetPassword(resetEmail)
            if (result.isSuccess) {
                errorMessage.value = "Password reset link sent to $resetEmail"
                onComplete(true)
            } else {
                errorMessage.value = "Failed to send reset email: ${result.exceptionOrNull()?.message}"
                onComplete(false)
            }
        }
    }

    fun clearUserData() {
        email.value = ""
        password.value = ""
        emailError.value = false
        passwordError.value = false
        errorMessage.value = null
    }
}