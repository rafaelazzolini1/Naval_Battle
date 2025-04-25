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
import com.example.navalbattle.ui.theme.screen.game.MenuScreen
import com.example.navalbattle.ui.theme.screen.game.MenuViewModel
import com.example.navalbattle.ui.theme.screen.login.LoginScreen
import com.example.navalbattle.ui.theme.screen.login.LoginViewModel

@Composable
fun AppNavigation(
    navController: NavHostController,
    isLightTheme: Boolean,
    loginViewModel: LoginViewModel,
    gameViewModel: GameViewModel,
    menuViewModel: MenuViewModel,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.fillMaxSize()
        ) {
            composable("login") {
                LoginScreenWithThemeToggle(
                    navController = navController,
                    isLightTheme = isLightTheme,
                    loginViewModel = loginViewModel,
                    onThemeToggle = onThemeToggle
                )
            }
            composable("menu") {
                MenuScreenWithThemeToggle(
                    navController = navController,
                    isLightTheme = isLightTheme,
                    gameViewModel = gameViewModel,
                    menuViewModel = menuViewModel,
                    onThemeToggle = onThemeToggle
                )
            }
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

@Composable
fun LoginScreenWithThemeToggle(
    navController: NavHostController,
    isLightTheme: Boolean,
    loginViewModel: LoginViewModel,
    onThemeToggle: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LoginScreen(navController, isLightTheme, loginViewModel)
        ThemeToggle(
            isLightTheme = isLightTheme,
            onThemeToggle = onThemeToggle,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(40.dp)
        )
    }
}

@Composable
fun MenuScreenWithThemeToggle(
    navController: NavHostController,
    isLightTheme: Boolean,
    gameViewModel: GameViewModel,
    menuViewModel: MenuViewModel,
    onThemeToggle: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        MenuScreen(
            isLightTheme = isLightTheme,
            gameViewModel = gameViewModel,
            menuViewModel = menuViewModel,
            navController = navController
        )
        ThemeToggle(
            isLightTheme = isLightTheme,
            onThemeToggle = onThemeToggle,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(40.dp)
        )
    }
}

@Composable
fun GameScreenWithThemeToggle(
    isLightTheme: Boolean,
    gameViewModel: GameViewModel,
    navController: NavHostController,
    onThemeToggle: (Boolean) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        GameScreen(isLightTheme, gameViewModel, navController)
    }
}

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