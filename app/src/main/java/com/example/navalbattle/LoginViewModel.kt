package com.example.navalbattle

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var emailError = mutableStateOf(false)
    var passwordError = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    // Método para limpar os dados do usuário
    fun clearUserData() {
        email.value = ""
        password.value = ""
        emailError.value = false
        passwordError.value = false
        errorMessage.value = null
    }
}