package com.example.navalbattle.ui.theme.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.navalbattle.R
import com.naval.battle.ui.component.CustomButton
import com.naval.battle.ui.component.CustomTextField

@Composable
fun LoginScreen(
    navController: NavController,
    isLightTheme: Boolean,
    viewModel: LoginViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isLightTheme) listOf(Color.White, Color(0xFFE0E0E0))
                    else listOf(Color(0xFF003087), Color(0xFF4FC3F7))
                )
            )
            .padding(24.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.intro),
            contentDescription = "Email Logo",
            modifier = Modifier
                .size(128.dp)
                .padding(bottom = 32.dp)
        )

        CustomTextField(
            value = viewModel.email.value,
            onValueChange = { viewModel.email.value = it },
            label = "Enter your email",
            isLightTheme = isLightTheme,
            isError = viewModel.emailError.value,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomTextField(
            value = viewModel.password.value,
            onValueChange = { viewModel.password.value = it },
            label = "Enter your password",
            isLightTheme = isLightTheme,
            isError = viewModel.passwordError.value,
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            text = "Login",
            onClick = {
                viewModel.signIn {
                    navController.navigate("game") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            },
            isLightTheme = isLightTheme
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomButton(
            text = "Register",
            onClick = { viewModel.register() },
            isLightTheme = isLightTheme
        )

        viewModel.errorMessage.value?.let {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = it,
                color = if (it.contains("successfully")) Color.Green else Color.Red,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}