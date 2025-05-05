package com.example.navalbattle.ui.theme.screen.game

import android.content.Context
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.navalbattle.R
import com.example.navalbattle.data.model.CellState
import com.example.navalbattle.data.model.Move
import com.google.firebase.auth.FirebaseAuth
import com.naval.battle.ui.component.CustomButton
import com.naval.battle.ui.component.GameBoard
import kotlinx.coroutines.delay
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GameScreen(
    isLightTheme: Boolean,
    viewModel: GameViewModel,
    navController: NavController
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    val cellSize = if (isLandscape) {
        (minOf(screenWidth * 0.4f, screenHeight * 0.7f) / 10).coerceAtMost(24.dp)
    } else {
        (screenWidth * 0.8f) / 10
    }

    val scrollState = rememberScrollState()
    val padding = if (isLandscape) 12.dp else 16.dp
    val spacerHeight = if (isLandscape) 8.dp else 16.dp
    val bottomSpacerHeight = if (isLandscape) 10.dp else 24.dp
    val topSpacerHeight = if (isLandscape) 32.dp else 48.dp

    val textStyle = if (isLandscape) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge
    val dialogTitleStyle = if (isLandscape) MaterialTheme.typography.titleMedium else MaterialTheme.typography.headlineMedium
    val dialogTextStyle = if (isLandscape) MaterialTheme.typography.bodyMedium else MaterialTheme.typography.bodyLarge

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
    val logoutButtonColor by animateColorAsState(
        targetValue = if (isLightTheme) Color(0xFFEF5350) else Color(0xFFD32F2F),
        animationSpec = tween(durationMillis = 900)
    )
    val cardBackgroundColor by animateColorAsState(
        targetValue = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.5f),
        animationSpec = tween(durationMillis = 900)
    )

    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""
    val playerScore by remember(viewModel.gameState.value.moves) {
        mutableStateOf(viewModel.gameState.value.moves.count { it.result == CellState.HIT && it.isPlayerMove })
    }
    val aiScore by remember(viewModel.gameState.value.moves) {
        mutableStateOf(viewModel.gameState.value.moves.count { it.result == CellState.HIT && !it.isPlayerMove })
    }
    val remainingShips by remember(viewModel.gameState.value.ships) {
        mutableStateOf(viewModel.gameState.value.ships.count { !it.isSunk })
    }

    var winner by rememberSaveable { mutableStateOf<String?>(null) }
    var showGameOverDialog by rememberSaveable { mutableStateOf(false) }
    var gameOverMessage by rememberSaveable { mutableStateOf("") }
    var gameEndedByMine by rememberSaveable { mutableStateOf(false) }
    var saveStatus by remember { mutableStateOf<String?>(null) }

    var timeLeft by rememberSaveable { mutableStateOf(viewModel.timeLimit) }
    var timerActive by rememberSaveable { mutableStateOf(true) }

    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val explosionSound by remember { mutableStateOf(MediaPlayer.create(context, R.raw.explosion)) }
    val victorySound by remember { mutableStateOf(MediaPlayer.create(context, R.raw.victory)) }
    val shipSunkSound by remember { mutableStateOf(MediaPlayer.create(context, R.raw.ship_sunk)) }

    fun playGameOverSound(isMine: Boolean) {
        if (isMine) {
            explosionSound?.start()
        } else {
            victorySound?.start()
        }
    }

    fun playShipSunkSound() {
        shipSunkSound?.start()
    }

    DisposableEffect(Unit) {
        onDispose {
            explosionSound?.release()
            victorySound?.release()
            shipSunkSound?.release()
        }
    }

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

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleCellClick(row: Int, col: Int) {
        if (winner != null || viewModel.isTimerPaused.value) return
        if (viewModel.gameState.value.board[row][col] == CellState.EMPTY) {
            viewModel.savePlayerMove(row, col, CellState.MISS) // Save move
            viewModel.gameState.value = viewModel.gameState.value.copy(
                board = viewModel.gameState.value.board.copyOf().apply { this[row][col] = CellState.MISS },
                moves = mutableListOf<Move>().apply {
                    addAll(viewModel.gameState.value.moves)
                    add(Move(row, col, CellState.MISS, true))
                },
                playerTurn = false
            )
        } else if (viewModel.gameState.value.board[row][col] == CellState.SHIP) {
            viewModel.savePlayerMove(row, col, CellState.HIT) // Save move
            val newBoard = viewModel.gameState.value.board.copyOf().apply { this[row][col] = CellState.HIT }
            val newMoves = viewModel.gameState.value.moves.toMutableList().apply {
                add(Move(row, col, CellState.HIT, true))
            }

            var isSunk = false
            val ship = viewModel.gameState.value.ships.find { ship ->
                val startRow = ship.startRow ?: return@find false
                val startCol = ship.startCol ?: return@find false
                val orientation = ship.orientation ?: return@find false
                val size = ship.size
                if (orientation == 0) {
                    row == startRow && col >= startCol && col < startCol + size
                } else {
                    col == startCol && row >= startRow && row < startRow + size
                }
            }

            if (ship != null) {
                val shipCells = mutableListOf<Pair<Int, Int>>()
                val startRow = ship.startRow!!
                val startCol = ship.startCol!!
                val orientation = ship.orientation!!
                val size = ship.size
                for (i in 0 until size) {
                    val r = if (orientation == 0) startRow else startRow + i
                    val c = if (orientation == 0) startCol + i else startCol
                    shipCells.add(r to c)
                }
                val hitCount = shipCells.count { (r, c) -> newBoard[r][c] == CellState.HIT }
                if (hitCount == size && !ship.isSunk) {
                    ship.isSunk = true
                    isSunk = true
                    vibrateDevice()
                    playShipSunkSound()
                }
            }

            if (viewModel.gameState.value.ships.all { it.isSunk }) {
                winner = if (isSunk) "Player" else "AI"
                gameOverMessage = if (isSunk) {
                    "Congratulations! You sank all enemy ships with $playerScore points!"
                } else {
                    "The AI has prevailed, sinking all your ships with $aiScore points!"
                }
                gameEndedByMine = false
                showGameOverDialog = true
                playGameOverSound(false)
                viewModel.saveGame(
                    playerScore = playerScore,
                    aiScore = aiScore,
                    winner = winner,
                    winnerScore = if (winner != null) (if (viewModel.gameState.value.lastSunkByPlayer) playerScore else aiScore) else 0,
                    onSuccess = { saveStatus = "Game and moves saved successfully! A confirmation email with the winner has been sent." },
                    onError = { error -> saveStatus = "Error saving game: $error" }
                )
            }

            viewModel.gameState.value = viewModel.gameState.value.copy(
                board = newBoard,
                moves = newMoves,
                lastSunkByPlayer = isSunk,
                playerTurn = false
            )
        } else if (viewModel.gameState.value.board[row][col] == CellState.MINE) {
            viewModel.savePlayerMove(row, col, CellState.MINE) // Save move
            val newBoard = viewModel.gameState.value.board.copyOf().apply { this[row][col] = CellState.MINE }
            val newMoves = viewModel.gameState.value.moves.toMutableList().apply {
                add(Move(row, col, CellState.MINE, true))
            }
            viewModel.gameState.value = viewModel.gameState.value.copy(
                board = newBoard,
                moves = newMoves,
                playerTurn = false
            )
            winner = "AI"
            gameOverMessage = "Oh no! You hit a mine and lost the battle. The AI wins with $aiScore points!"
            gameEndedByMine = true
            showGameOverDialog = true
            playGameOverSound(true)
            viewModel.saveGame(
                playerScore = playerScore,
                aiScore = aiScore,
                winner = winner,
                winnerScore = aiScore,
                onSuccess = { saveStatus = "Game and moves saved successfully! A confirmation email with the winner has been sent." },
                onError = { error -> saveStatus = "Error saving game: $error" }
            )
        }
        timeLeft = viewModel.timeLimit
    }

    LaunchedEffect(timerActive, viewModel.gameState.value.playerTurn, viewModel.isTimerPaused.value) {
        if (timerActive && viewModel.gameState.value.playerTurn && winner == null && !viewModel.isTimerPaused.value) {
            while (timeLeft > 0) {
                delay(1000L)
                if (!viewModel.isTimerPaused.value) {
                    timeLeft -= 1
                }
            }
            if (timeLeft == 0) {
                var row: Int
                var col: Int
                do {
                    row = Random.nextInt(0, 10)
                    col = Random.nextInt(0, 10)
                } while (viewModel.gameState.value.board[row][col] == CellState.HIT ||
                    viewModel.gameState.value.board[row][col] == CellState.MISS)
                handleCellClick(row, col)
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
        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoard(
                        gameState = viewModel.gameState.value,
                        cellSize = cellSize,
                        isLightTheme = isLightTheme,
                        gameOver = showGameOverDialog,
                        onCellClick = { row, col -> handleCellClick(row, col) }
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .verticalScroll(scrollState)
                        .padding(start = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.height(topSpacerHeight))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Player: $userEmail",
                            style = MaterialTheme.typography.titleMedium,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = if (viewModel.isTimerPaused.value) "Resume" else "Pause",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (winner == null) textColor else textColor.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = if (winner == null) textColor else textColor.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable(enabled = winner == null) { viewModel.toggleTimerPause() }
                            )
                            Text(
                                text = "Log Out",
                                style = MaterialTheme.typography.labelMedium,
                                color = logoutButtonColor,
                                modifier = Modifier
                                    .border(
                                        width = 1.dp,
                                        color = logoutButtonColor,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                                    .clickable {
                                        viewModel.resetGame(5)
                                        FirebaseAuth.getInstance().signOut()
                                        navController.navigate("login") {
                                            popUpTo("game") { inclusive = true }
                                        }
                                    }
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = cardBackgroundColor
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                text = "Player: $playerScore",
                                color = textColor,
                                style = textStyle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "AI: $aiScore",
                                color = textColor,
                                style = textStyle
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Remaining Ships: $remainingShips",
                                color = textColor,
                                style = textStyle
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Sunk Ships:",
                                color = textColor,
                                style = textStyle.copy(fontSize = if (isLandscape) 10.sp else 14.sp)
                            )
                            viewModel.gameState.value.ships.filter { it.isSunk }.forEach { ship ->
                                val sunkBy = if (viewModel.gameState.value.lastSunkByPlayer) "Player" else "AI"
                                Text(
                                    text = "- ${ship.name} (Sunk by $sunkBy)",
                                    color = textColor,
                                    style = textStyle.copy(fontSize = if (isLandscape) 8.sp else 12.sp)
                                )
                            }
                            if (viewModel.gameState.value.ships.none { it.isSunk }) {
                                Text(
                                    text = "None",
                                    color = textColor,
                                    style = textStyle.copy(fontSize = if (isLandscape) 8.sp else 12.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Remaining Ships:",
                                color = textColor,
                                style = textStyle.copy(fontSize = if (isLandscape) 10.sp else 14.sp)
                            )
                            viewModel.gameState.value.ships.filterNot { it.isSunk }.forEach { ship ->
                                Text(
                                    text = "- ${ship.name}",
                                    color = textColor,
                                    style = textStyle.copy(fontSize = if (isLandscape) 8.sp else 12.sp)
                                )
                            }
                            if (viewModel.gameState.value.ships.all { it.isSunk }) {
                                Text(
                                    text = "None",
                                    color = textColor,
                                    style = textStyle.copy(fontSize = if (isLandscape) 8.sp else 12.sp)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            if (viewModel.gameState.value.playerTurn && !showGameOverDialog) {
                                Box(
                                    modifier = Modifier
                                        .height(24.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        text = "Time Remaining: $timeLeft s",
                                        color = if (timeLeft <= 3) Color.Red else textColor,
                                        style = textStyle.copy(fontSize = if (isLandscape) 10.sp else 14.sp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacerHeight))

                    CustomButton(
                        text = "Restart Game",
                        onClick = {
                            navController.navigate("menu")
                            winner = null
                            showGameOverDialog = false
                            timerActive = false
                            timeLeft = viewModel.timeLimit
                        },
                        isLightTheme = isLightTheme,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.height(topSpacerHeight))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Player: $userEmail",
                        style = MaterialTheme.typography.titleLarge,
                        color = textColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (viewModel.isTimerPaused.value) "Resume" else "Pause",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (winner == null) textColor else textColor.copy(alpha = 0.5f),
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = if (winner == null) textColor else textColor.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable(enabled = winner == null) { viewModel.toggleTimerPause() }
                        )
                        Text(
                            text = "Log Out",
                            style = MaterialTheme.typography.bodyMedium,
                            color = logoutButtonColor,
                            modifier = Modifier
                                .border(
                                    width = 1.dp,
                                    color = logoutButtonColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .clickable {
                                    viewModel.resetGame(5)
                                    FirebaseAuth.getInstance().signOut()
                                    navController.navigate("login") {
                                        popUpTo("game") { inclusive = true }
                                    }
                                }
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = cardBackgroundColor
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = "Player: $playerScore",
                                color = textColor,
                                style = textStyle
                            )
                            Text(
                                text = "AI: $aiScore",
                                color = textColor,
                                style = textStyle
                            )
                            Text(
                                text = "Ships: $remainingShips",
                                color = textColor,
                                style = textStyle
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Sunk Ships:",
                            color = textColor,
                        )
                        viewModel.gameState.value.ships.filter { it.isSunk }.forEach { ship ->
                            val sunkBy = if (viewModel.gameState.value.lastSunkByPlayer) "Player" else "AI"
                            Text(
                                text = "- ${ship.name} (Sunk by $sunkBy)",
                                color = textColor,
                            )
                        }
                        if (viewModel.gameState.value.ships.none { it.isSunk }) {
                            Text(
                                text = "None",
                                color = textColor,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Remaining Ships:",
                            color = textColor,
                        )
                        viewModel.gameState.value.ships.filterNot { it.isSunk }.forEach { ship ->
                            Text(
                                text = "- ${ship.name}",
                                color = textColor,
                            )
                        }
                        if (viewModel.gameState.value.ships.all { it.isSunk }) {
                            Text(
                                text = "None",
                                color = textColor,
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        if (viewModel.gameState.value.playerTurn && !showGameOverDialog) {
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = "Time Remaining: $timeLeft s",
                                    color = if (timeLeft <= 3) Color.Red else textColor,
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacerHeight))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    contentAlignment = Alignment.Center
                ) {
                    GameBoard(
                        gameState = viewModel.gameState.value,
                        cellSize = cellSize,
                        isLightTheme = isLightTheme,
                        gameOver = showGameOverDialog,
                        onCellClick = { row, col -> handleCellClick(row, col) }
                    )
                }

                Spacer(modifier = Modifier.height(spacerHeight))

                CustomButton(
                    text = "Restart Game",
                    onClick = {
                        navController.navigate("menu")
                        winner = null
                        showGameOverDialog = false
                        timerActive = false
                        timeLeft = viewModel.timeLimit
                    },
                    isLightTheme = isLightTheme,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(bottomSpacerHeight))
            }
        }
    }

    if (showGameOverDialog) {
        LaunchedEffect(Unit) {
            delay(1000L)
            // After 1 second, keep dialog open for user interaction
        }

        AlertDialog(
            onDismissRequest = { }, // Prevent dismissing by clicking outside
            title = {
                Text(
                    text = if (winner == "Player") "Victory Achieved!" else "Battle Lost!",
                    style = dialogTitleStyle,
                    color = if (winner == "Player") Color(0xFF4CAF50) else Color(0xFFD32F2F),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = gameOverMessage,
                        style = dialogTextStyle,
                        textAlign = TextAlign.Center,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    saveStatus?.let {
                        Text(
                            text = it,
                            style = dialogTextStyle.copy(fontSize = 14.sp),
                            color = if (it.contains("Error")) Color.Red else Color(0xFF4CAF50),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },

            dismissButton = {
                CustomButton(
                    text = "Log Out",
                    onClick = {
                        viewModel.resetGame(5)
                        FirebaseAuth.getInstance().signOut()
                        navController.navigate("login") {
                            popUpTo("game") { inclusive = true }
                        }
                    },
                    isLightTheme = isLightTheme,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .fillMaxWidth(0.45f)
                )
            },

            confirmButton = {
                CustomButton(
                    text = "Restart",
                    onClick = {
                        navController.navigate("menu")
                        winner = null
                        showGameOverDialog = false
                        timerActive = false
                        timeLeft = viewModel.timeLimit
                    },
                    isLightTheme = isLightTheme,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .fillMaxWidth(0.45f)
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .padding(16.dp)
        )
    }

    if (!viewModel.gameState.value.playerTurn && winner == null) {
        LaunchedEffect(Unit) {
            val (row, col) = viewModel.aiTurn()
            val newBoard = viewModel.gameState.value.board.copyOf()
            val newMoves = viewModel.gameState.value.moves.toMutableList()

            if (viewModel.gameState.value.board[row][col] == CellState.SHIP) {
                newBoard[row][col] = CellState.HIT
                newMoves.add(Move(row, col, CellState.HIT, false))

                var isSunk = false
                val ship = viewModel.gameState.value.ships.find { ship ->
                    val startRow = ship.startRow ?: return@find false
                    val startCol = ship.startCol ?: return@find false
                    val orientation = ship.orientation ?: return@find false
                    val size = ship.size
                    if (orientation == 0) {
                        row == startRow && col >= startCol && col < startCol + size
                    } else {
                        col == startCol && row >= startRow && row < startRow + size
                    }
                }

                if (ship != null) {
                    val shipCells = mutableListOf<Pair<Int, Int>>()
                    val startRow = ship.startRow!!
                    val startCol = ship.startCol!!
                    val orientation = ship.orientation!!
                    val size = ship.size
                    for (i in 0 until size) {
                        val r = if (orientation == 0) startRow else startRow + i
                        val c = if (orientation == 0) startCol + i else startCol
                        shipCells.add(r to c)
                    }
                    val hitCount = shipCells.count { (r, c) -> newBoard[r][c] == CellState.HIT }
                    if (hitCount == size && !ship.isSunk) {
                        ship.isSunk = true
                        isSunk = true
                        vibrateDevice()
                        playShipSunkSound()
                    }
                }

                if (viewModel.gameState.value.ships.all { it.isSunk }) {
                    winner = if (isSunk) "Player" else "AI"
                    gameOverMessage = if (isSunk) {
                        "Congratulations! You sank all enemy ships with $playerScore points!"
                    } else {
                        "The AI has prevailed, sinking all your ships with $aiScore points!"
                    }
                    gameEndedByMine = false
                    showGameOverDialog = true
                    playGameOverSound(false)
                    viewModel.saveGame(
                        playerScore = playerScore,
                        aiScore = aiScore,
                        winner = winner,
                        winnerScore = if (winner != null) (if (viewModel.gameState.value.lastSunkByPlayer) playerScore else aiScore) else 0,
                        onSuccess = { saveStatus = "Game and moves saved successfully! A confirmation email with the winner has been sent." },
                        onError = { error -> saveStatus = "Error saving game: $error" }
                    )
                }

                viewModel.gameState.value = viewModel.gameState.value.copy(
                    board = newBoard,
                    moves = newMoves,
                    lastSunkByPlayer = !isSunk,
                    playerTurn = true
                )
                timerActive = true
                timeLeft = viewModel.timeLimit
            } else if (viewModel.gameState.value.board[row][col] == CellState.MINE) {
                newBoard[row][col] = CellState.MINE
                newMoves.add(Move(row, col, CellState.MINE, false))
                viewModel.gameState.value = viewModel.gameState.value.copy(
                    board = newBoard,
                    moves = newMoves,
                    playerTurn = true
                )
                winner = "Player"
                gameOverMessage = "The AI triggered a mine! You win with $playerScore points!"
                gameEndedByMine = true
                showGameOverDialog = true
                playGameOverSound(true)
                viewModel.saveGame(
                    playerScore = playerScore,
                    aiScore = aiScore,
                    winner = winner,
                    winnerScore = playerScore,
                    onSuccess = { saveStatus = "Game and moves saved successfully! A confirmation email with the winner has been sent." },
                    onError = { error -> saveStatus = "Error saving game: $error" }
                )
            } else {
                newBoard[row][col] = CellState.MISS
                newMoves.add(Move(row, col, CellState.MISS, false))
                viewModel.gameState.value = viewModel.gameState.value.copy(
                    board = newBoard,
                    moves = newMoves,
                    playerTurn = true
                )
                timerActive = true
                timeLeft = viewModel.timeLimit
            }
        }
    }
}