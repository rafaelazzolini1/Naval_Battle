package com.example.navalbattle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.navalbattle.ui.navigation.AppNavigation
import com.example.navalbattle.ui.theme.NavalBattleTheme
import com.example.navalbattle.ui.theme.screen.game.GameViewModel
import com.example.navalbattle.ui.theme.screen.game.MenuViewModel
import com.example.navalbattle.ui.theme.screen.login.LoginViewModel
import com.example.navalbattle.util.LightSensorManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naval.battle.data.repository.AuthRepository
import com.naval.battle.data.repository.GameRepository

/**
 * Main activity for the Naval Battle game, responsible for initializing the UI,
 * managing theme state, and integrating sensor and navigation logic.
 */
class MainActivity : ComponentActivity() {
    // Lazily initialized repositories for authentication and game data
    private val authRepository by lazy { AuthRepository(FirebaseAuth.getInstance()) }
    private val gameRepository by lazy {
        GameRepository(
            FirebaseFirestore.getInstance()
        )
    }

    // ViewModels for login, game, and menu screens, initialized with custom factories
    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(authRepository) }
    private val gameViewModel: GameViewModel by viewModels { GameViewModelFactory(gameRepository) }
    private val menuViewModel: MenuViewModel by viewModels()

    /**
     * Called when the activity is created. Sets up the UI with theme management
     * and navigation, using a light sensor for dynamic theme switching.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize light sensor manager
        val lightSensorManager = LightSensorManager(this)

        setContent {
            // State for tracking light/dark theme, preserved across configuration changes
            var isLightTheme by rememberSaveable { mutableStateOf(true) }
            // State to track if the theme was manually set by the user
            var isThemeManuallySet by rememberSaveable { mutableStateOf(false) }

            // Collect theme updates from the sensor
            val sensorTheme by lightSensorManager.isLightTheme.collectAsState()

            // Update theme based on sensor only if not manually set and sensor is active
            LaunchedEffect(sensorTheme) {
                if (lightSensorManager.isSensorActive && !isThemeManuallySet) {
                    isLightTheme = sensorTheme
                }
            }

            // Create NavController
            val navController = rememberNavController()

            // Apply the Naval Battle theme with dynamic light/dark mode
            NavalBattleTheme(isLightTheme = isLightTheme) {
                AppNavigation(
                    navController = navController,
                    isLightTheme = isLightTheme,
                    loginViewModel = loginViewModel,
                    gameViewModel = gameViewModel,
                    menuViewModel = menuViewModel,
                    onThemeToggle = { newTheme ->
                        isLightTheme = newTheme
                        isThemeManuallySet = true
                        lightSensorManager.setThemeManually(newTheme)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        // Start listening to sensor events after a short delay
        lightSensorManager.startListening()
    }

    /**
     * Called when the activity is destroyed. Stops sensor listening to prevent resource leaks.
     */
    override fun onDestroy() {
        super.onDestroy()
        // Ensure lightSensorManager is accessible; reinitialize if needed
        val lightSensorManager = LightSensorManager(this)
        lightSensorManager.stopListening()
    }
}

/**
 * Factory class for creating LoginViewModel instances.
 *
 * @param authRepository Repository for handling authentication logic
 */
class LoginViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

/**
 * Factory class for creating GameViewModel instances.
 *
 * @param gameRepository Repository for handling game data logic
 */
class GameViewModelFactory(private val gameRepository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}