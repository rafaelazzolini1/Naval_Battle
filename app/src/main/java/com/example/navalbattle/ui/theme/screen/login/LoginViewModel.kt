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

    fun register() {
        emailError.value = email.value.isEmpty()
        passwordError.value = password.value.isEmpty()

        if (!emailError.value && !passwordError.value) {
            viewModelScope.launch {
                val result = authRepository.register(email.value, password.value)
                if (result.isSuccess) {
                    errorMessage.value = "User registered successfully!"
                } else {
                    errorMessage.value = result.exceptionOrNull()?.message ?: "Registration failed"
                }
            }
        } else {
            errorMessage.value = "Please fill in all fields!"
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