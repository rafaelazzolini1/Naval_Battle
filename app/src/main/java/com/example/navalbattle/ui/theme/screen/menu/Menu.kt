package com.example.navalbattle.ui.theme.screen.game

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.naval.battle.ui.component.CustomButton

@Composable
fun MenuScreen(
    isLightTheme: Boolean,
    gameViewModel: GameViewModel,
    menuViewModel: MenuViewModel,
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val padding = if (isLandscape) 8.dp else 12.dp
    val textStyle = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    val dialogTitleStyle = if (isLandscape) MaterialTheme.typography.titleSmall else MaterialTheme.typography.headlineSmall
    val dialogTextStyle = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium
    val titleStyle = if (isLandscape) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineSmall

    val gradientStartColor by animateColorAsState(
        targetValue = if (isLightTheme) Color.White else Color(0xFF003087),
        animationSpec = tween(durationMillis = 800)
    )
    val gradientEndColor by animateColorAsState(
        targetValue = if (isLightTheme) Color(0xFFE0E0E0) else Color(0xFF4FC3F7),
        animationSpec = tween(durationMillis = 800)
    )
    val textColor by animateColorAsState(
        targetValue = if (isLightTheme) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 900)
    )
    val cardBackgroundColor by animateColorAsState(
        targetValue = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 900)
    )

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    var showTutorialDialog by remember { mutableStateOf(false) }

    fun vibrateDevice() {
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                vibrator.vibrate(100)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientStartColor, gradientEndColor)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Game Settings",
                    style = titleStyle,
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackgroundColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Number of Mines (1-10)",
                            style = textStyle,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Default: 5 mines",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        TextField(
                            value = menuViewModel.minesInput,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                                    menuViewModel.updateMinesInput(newValue)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, textColor, RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Timer Duration (1-10 seconds)",
                            style = textStyle,
                            color = textColor,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Default: 7 seconds",
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        TextField(
                            value = menuViewModel.timerInput,
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } && newValue.length <= 2) {
                                    menuViewModel.updateTimerInput(newValue)
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, textColor, RoundedCornerShape(8.dp))
                                .background(Color.Transparent)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomButton(
                        text = "Start Game",
                        onClick = {
                            val numberOfMines = menuViewModel.minesInput.toIntOrNull() ?: 5
                            val validMines = numberOfMines.coerceIn(1, 10)
                            val timerSeconds = menuViewModel.timerInput.toIntOrNull() ?: 7
                            val validTimer = timerSeconds.coerceIn(1, 10)
                            gameViewModel.resetGame(validMines)
                            gameViewModel.updateTimerSettings(validTimer)
                            vibrateDevice()
                            navController.navigate("game")
                        },
                        isLightTheme = isLightTheme,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    CustomButton(
                        text = "How to Play",
                        onClick = {
                            showTutorialDialog = true
                            vibrateDevice()
                        },
                        isLightTheme = isLightTheme,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                CustomButton(
                    text = "Log Out",
                    onClick = {
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("menu") { inclusive = true }
                        }
                    },
                    isLightTheme = isLightTheme,
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }

        if (showTutorialDialog) {
            AlertDialog(
                onDismissRequest = { showTutorialDialog = false },
                title = {
                    Text(
                        text = "How to Play Naval Battle",
                        style = dialogTitleStyle,
                        color = textColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Objective:",
                            style = dialogTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "Sink all enemy ships before they sink yours or you hit a mine!",
                            style = dialogTextStyle,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Gameplay:",
                            style = dialogTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "- The game is played on a 10x10 grid.\n" +
                                    "- You and the AI take turns selecting grid cells.\n" +
                                    "- Each turn has a timer (default 7 seconds). If time runs out, a random cell is chosen.\n" +
                                    "- Cells can contain ships, mines, or be empty.",
                            style = dialogTextStyle,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Cell Types:",
                            style = dialogTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "- Ship: Hit to damage enemy ships. Sink all to win!\n" +
                                    "- Mine: Hitting a mine ends the game with your loss.\n" +
                                    "- Empty: Results in a miss, no effect.",
                            style = dialogTextStyle,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Scoring:",
                            style = dialogTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "- Earn points for each ship cell hit.\n" +
                                    "- The game saves your progress and sends a winner announcement email.",
                            style = dialogTextStyle,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Controls:",
                            style = dialogTextStyle.copy(fontWeight = FontWeight.Bold),
                            color = textColor
                        )
                        Text(
                            text = "- Tap a grid cell to attack.\n" +
                                    "- Use the Pause button to pause the timer.\n" +
                                    "- Restart or log out from the game screen.",
                            style = dialogTextStyle,
                            color = textColor
                        )
                    }
                },
                confirmButton = {
                    CustomButton(
                        text = "Close",
                        onClick = { showTutorialDialog = false },
                        isLightTheme = isLightTheme,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .padding(16.dp)
            )
        }
    }
}