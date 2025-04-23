package com.example.navalbattle.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.navalbattle.ui.theme.screen.login.LoginScreen
import com.example.navalbattle.ui.theme.screen.login.LoginViewModel

/**
 * Composable function that displays the login screen with a theme toggle button.
 *
 * @param navController Navigation controller for screen transitions
 * @param isLightTheme Whether the light theme is currently active
 * @param loginViewModel ViewModel for handling login logic
 * @param onThemeToggle Callback to toggle between light and dark themes
 */
@Composable
fun LoginScreenWithThemeToggle(
    navController: NavHostController,
    isLightTheme: Boolean,
    loginViewModel: LoginViewModel,
    onThemeToggle: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Render the login screen
        LoginScreen(navController, isLightTheme, loginViewModel)

        // Theme toggle button at the bottom-right corner
        ThemeToggle(
            isLightTheme = isLightTheme,
            onThemeToggle = onThemeToggle,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(40.dp)
        )
    }
}