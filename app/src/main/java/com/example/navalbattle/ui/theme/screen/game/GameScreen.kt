package com.example.navalbattle.ui.theme.screen.game

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.navalbattle.data.model.CellState
import com.example.navalbattle.data.model.Move
import com.google.firebase.auth.FirebaseAuth
import com.naval.battle.ui.component.CustomButton
import com.naval.battle.ui.component.GameBoard

@Composable
fun GameScreen(
    isLightTheme: Boolean,
    viewModel: GameViewModel,
    navController: NavController
) {
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: "Unknown User"
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val cellSize = (screenWidth - 64.dp) / 10
    val scrollState = rememberScrollState()

    val playerScore by remember(viewModel.gameState.value.moves) {
        mutableStateOf(viewModel.gameState.value.moves.count { it.result == CellState.HIT && it.isPlayerMove })
    }
    val aiScore by remember(viewModel.gameState.value.moves) {
        mutableStateOf(viewModel.gameState.value.moves.count { it.result == CellState.HIT && !it.isPlayerMove })
    }
    val remainingShips by remember(viewModel.gameState.value.ships) {
        mutableStateOf(viewModel.gameState.value.ships.count { !it.isSunk })
    }
    val winner by remember(remainingShips) {
        mutableStateOf(
            if (remainingShips == 0) {
                if (viewModel.gameState.value.lastSunkByPlayer) "Player" else "AI"
            } else {
                null
            }
        )
    }

    var saveMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (isLightTheme) listOf(Color.White, Color(0xFFE0E0E0))
                    else listOf(Color(0xFF003087), Color(0xFF4FC3F7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Player: $userEmail",
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isLightTheme) Color.Black else Color.White
                )

                Text(
                    text = "Logout",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isLightTheme) Color(0xFFEF5350) else Color(0xFFD32F2F),
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = if (isLightTheme) Color(0xFFEF5350) else Color(0xFFD32F2F),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .clickable {
                            viewModel.resetGame()
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate("login") {
                                popUpTo("game") { inclusive = true }
                            }
                        }
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF1E3A8A).copy(alpha = 0.5f)
                    )
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(
                    text = "Player: $playerScore",
                    color = if (isLightTheme) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "AI: $aiScore",
                    color = if (isLightTheme) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Remaining Ships: $remainingShips",
                    color = if (isLightTheme) Color.Black else Color.White,
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            GameBoard(
                gameState = viewModel.gameState.value,
                cellSize = cellSize,
                isLightTheme = isLightTheme,
                onCellClick = { row, col ->
                    if (viewModel.gameState.value.board[row][col] == CellState.EMPTY) {
                        viewModel.gameState.value = viewModel.gameState.value.copy(
                            board = viewModel.gameState.value.board.copyOf().apply { this[row][col] = CellState.MISS },
                            moves = viewModel.gameState.value.moves.apply { add(Move(row, col, CellState.MISS, true)) },
                            playerTurn = false
                        )
                    } else if (viewModel.gameState.value.board[row][col] == CellState.SHIP) {
                        val newBoard = viewModel.gameState.value.board.copyOf().apply { this[row][col] = CellState.HIT }
                        val newMoves = viewModel.gameState.value.moves.apply { add(Move(row, col, CellState.HIT, true)) }
                        val shipCells = mutableListOf<Pair<Int, Int>>()
                        for (i in newBoard.indices) {
                            for (j in newBoard[i].indices) {
                                if (newBoard[i][j] == CellState.SHIP || newBoard[i][j] == CellState.HIT) {
                                    shipCells.add(i to j)
                                }
                            }
                        }
                        val hitCount = shipCells.count { (r, c) -> newBoard[r][c] == CellState.HIT }
                        val ship = viewModel.gameState.value.ships.find { it.size == shipCells.size && !it.isSunk }
                        val isSunk = ship != null && hitCount == ship.size
                        if (isSunk) {
                            ship?.isSunk = true
                        }
                        viewModel.gameState.value = viewModel.gameState.value.copy(
                            board = newBoard,
                            moves = newMoves,
                            lastSunkByPlayer = isSunk,
                            playerTurn = false
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            CustomButton(
                text = "Restart Game",
                onClick = { viewModel.resetGame() },
                isLightTheme = isLightTheme
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomButton(
                text = "Save Game",
                onClick = { viewModel.showSaveDialog.value = true },
                isLightTheme = isLightTheme
            )

            if (viewModel.showSaveDialog.value) {
                AlertDialog(
                    onDismissRequest = { viewModel.showSaveDialog.value = false },
                    title = { Text("Confirm Save", style = MaterialTheme.typography.headlineSmall) },
                    text = { Text("Do you want to save this game?", style = MaterialTheme.typography.bodyLarge) },
                    confirmButton = {
                        CustomButton(
                            text = "Yes",
                            onClick = {
                                viewModel.saveGame(
                                    playerScore = playerScore,
                                    aiScore = aiScore,
                                    winner = winner,
                                    winnerScore = if (winner != null) (if (viewModel.gameState.value.lastSunkByPlayer) playerScore else aiScore) else 0,
                                    onSuccess = { saveMessage = "Game saved successfully! Winner: ${winner ?: "None"} with ${(if (viewModel.gameState.value.lastSunkByPlayer) playerScore else aiScore)} points." },
                                    onError = { error -> saveMessage = "Error saving game: $error" }
                                )
                                viewModel.showSaveDialog.value = false
                            },
                            isLightTheme = isLightTheme,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        )
                    },
                    dismissButton = {
                        CustomButton(
                            text = "No",
                            onClick = { viewModel.showSaveDialog.value = false },
                            isLightTheme = isLightTheme,
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }

            saveMessage?.let {
                Text(
                    text = it,
                    color = if (it.contains("Error")) Color.Red else Color.Green,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (!viewModel.gameState.value.playerTurn) {
        LaunchedEffect(Unit) {
            val (row, col) = viewModel.aiTurn()
            val newBoard = viewModel.gameState.value.board.copyOf().apply {
                this[row][col] = if (this[row][col] == CellState.SHIP) CellState.HIT else CellState.MISS
            }
            val newMoves = viewModel.gameState.value.moves.apply {
                add(Move(row, col, if (viewModel.gameState.value.board[row][col] == CellState.SHIP) CellState.HIT else CellState.MISS, false))
            }
            val shipCells = mutableListOf<Pair<Int, Int>>()
            for (i in newBoard.indices) {
                for (j in newBoard[i].indices) {
                    if (newBoard[i][j] == CellState.SHIP || newBoard[i][j] == CellState.HIT) {
                        shipCells.add(i to j)
                    }
                }
            }
            val hitCount = shipCells.count { (r, c) -> newBoard[r][c] == CellState.HIT }
            val ship = viewModel.gameState.value.ships.find { it.size == shipCells.size && !it.isSunk }
            val isSunk = ship != null && hitCount == ship.size
            if (isSunk) {
                ship?.isSunk = true
            }
            viewModel.gameState.value = viewModel.gameState.value.copy(
                board = newBoard,
                moves = newMoves,
                lastSunkByPlayer = !isSunk,
                playerTurn = true
            )
        }
    }
}