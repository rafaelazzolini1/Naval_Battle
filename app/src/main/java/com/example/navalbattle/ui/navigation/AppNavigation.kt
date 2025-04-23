package com.example.navalbattle.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.navalbattle.ui.theme.screen.game.GameScreen
import com.example.navalbattle.ui.theme.screen.game.GameViewModel
import com.example.navalbattle.ui.theme.screen.login.LoginScreen
import com.example.navalbattle.ui.theme.screen.login.LoginViewModel

/**
 * Composable that sets up the navigation host for the app, defining routes for login and game screens.
 *
 * @param navController Navigation controller for screen transitions
 * @param isLightTheme Whether the light theme is currently active
 * @param loginViewModel ViewModel for handling login logic
 * @param gameViewModel ViewModel for handling game logic
 * @param onThemeToggle Callback to toggle between light and dark themes
 * @param modifier Modifier for styling the navigation host
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    isLightTheme: Boolean,
    loginViewModel: LoginViewModel,
    gameViewModel: GameViewModel,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.fillMaxSize()
        ) {
            // Login screen route
            composable("login") {
                LoginScreenWithThemeToggle(
                    navController = navController,
                    isLightTheme = isLightTheme,
                    loginViewModel = loginViewModel,
                    onThemeToggle = onThemeToggle
                )
            }
            // Game screen route
            composable("game") {
                GameScreenWithThemeToggle(
                    isLightTheme = isLightTheme,
                    gameViewModel = gameViewModel,
                    navController = navController,
                    onThemeToggle = onThemeToggle
                )
            }
        }
    }
}

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

/**
 * Composable function that displays the game screen with a theme toggle button.
 *
 * @param isLightTheme Whether the light theme is currently active
 * @param gameViewModel ViewModel for handling game logic
 * @param navController Navigation controller for screen transitions
 * @param onThemeToggle Callback to toggle between light and dark themes
 */
@Composable
fun GameScreenWithThemeToggle(
    isLightTheme: Boolean,
    gameViewModel: GameViewModel,
    navController: NavHostController,
    onThemeToggle: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Render the game screen
        GameScreen(isLightTheme, gameViewModel, navController)

        // Theme toggle button at the bottom-right corner (uncomment if needed)
        /*
        ThemeToggle(
            isLightTheme = isLightTheme,
            onThemeToggle = onThemeToggle,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(26.dp)
        )
        */
    }
}

/**
 * Composable function for a theme toggle button that switches between light and dark modes.
 *
 * @param isLightTheme Whether the light theme is currently active
 * @param onThemeToggle Callback to toggle between light and dark themes
 * @param modifier Modifier for styling and positioning the button
 */
@Composable
fun ThemeToggle(
    isLightTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onThemeToggle(!isLightTheme) },
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                color = if (isLightTheme)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            )
    ) {
        // Display light or dark mode icon based on current theme
        Icon(
            imageVector = if (isLightTheme) Icons.Filled.LightMode else Icons.Filled.NightlightRound,
            contentDescription = "Toggle theme",
            tint = if (isLightTheme)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}