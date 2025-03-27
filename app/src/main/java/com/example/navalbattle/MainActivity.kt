package com.example.navalbattle;

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.navalbattle.ui.theme.NavalBattleTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.naval.battle.data.repository.AuthRepository
import com.naval.battle.data.repository.GameRepository
import com.naval.battle.ui.component.CustomButton
import com.example.navalbattle.ui.theme.screen.game.GameScreen
import com.example.navalbattle.ui.theme.screen.game.GameViewModel
import com.example.navalbattle.ui.theme.screen.login.LoginScreen
import com.example.navalbattle.ui.theme.screen.login.LoginViewModel

class MainActivity : ComponentActivity() {
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    private val authRepository by lazy { AuthRepository(FirebaseAuth.getInstance()) }
    private val gameRepository by lazy { GameRepository(FirebaseFirestore.getInstance()) }

    private val loginViewModel: LoginViewModel by viewModels { LoginViewModelFactory(authRepository) }
    private val gameViewModel: GameViewModel by viewModels { GameViewModelFactory(gameRepository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        setContent {
            var isLightTheme by remember { mutableStateOf(true) }

            val lightSensorListener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent?) {
                    if (event?.sensor?.type == Sensor.TYPE_LIGHT) {
                        val lux = event.values[0]
                        isLightTheme = lux > 20000
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
            }

            LaunchedEffect(Unit) {
                lightSensor?.also { light ->
                    sensorManager.registerListener(
                        lightSensorListener,
                        light,
                        SensorManager.SENSOR_DELAY_NORMAL
                    )
                }
            }

            NavalBattleTheme(isLightTheme = isLightTheme) {
                Box(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CustomButton(
                            text = if (isLightTheme) "Switch to Dark Theme" else "Switch to Light Theme",
                            onClick = { isLightTheme = !isLightTheme },
                            isLightTheme = isLightTheme,
                            modifier = Modifier
                                .padding(16.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )

                        NavHost(navController = navController, startDestination = "login") {
                            composable("login") { LoginScreen(navController, isLightTheme, loginViewModel) }
                            composable("game") { GameScreen(isLightTheme, gameViewModel, navController) }
                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {}
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        })
    }
}

class LoginViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class GameViewModelFactory(private val gameRepository: GameRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(gameRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}