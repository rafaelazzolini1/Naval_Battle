package com.example.navalbattle.ui.theme.screen.game

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navalbattle.R
import com.example.navalbattle.data.model.CellState
import com.example.navalbattle.data.model.GameState
import com.example.navalbattle.data.model.Ship
import com.google.firebase.auth.FirebaseAuth
import com.naval.battle.data.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {
    val gameState = mutableStateOf(GameState())
    var timeLimit = 7 // Default value
        private set
    val isTimerPaused = mutableStateOf(false) // New state for pausing timer

    fun resetGame(numberOfMines: Int) {
        val ships = listOf(
            Ship(
                size = 5,
                name = "Aircraft Carrier",
                horizontalImageResId = R.drawable.aircraft_horizontal,
                verticalImageResId = R.drawable.aircraft_vertical
            ),
            Ship(
                size = 4,
                name = "Battleship",
                horizontalImageResId = R.drawable.battleship_horizontal,
                verticalImageResId = R.drawable.battleship_vertical
            ),
            Ship(
                size = 3,
                name = "Cruiser",
                horizontalImageResId = R.drawable.cruiser_horizontal,
                verticalImageResId = R.drawable.cruiser_vertical
            ),
            Ship(
                size = 3,
                name = "Submarine",
                horizontalImageResId = R.drawable.submarine_horizontal,
                verticalImageResId = R.drawable.submarine_vertical
            ),
            Ship(
                size = 2,
                name = "Destroyer",
                horizontalImageResId = R.drawable.destroyer_horizontal,
                verticalImageResId = R.drawable.destroyer_vertical
            )
        )
        val initialBoard = Array(10) { Array(10) { CellState.EMPTY } }
        placeShips(initialBoard, ships)
        val minePositions = placeMines(initialBoard, numberOfMines)
        gameState.value = GameState(
            board = initialBoard,
            ships = ships,
            mines = minePositions,
            moves = mutableListOf(),
            playerTurn = true,
            lastSunkByPlayer = false
        )
    }

    fun updateTimerSettings(newTimeLimit: Int) {
        timeLimit = newTimeLimit
    }

    fun toggleTimerPause() {
        isTimerPaused.value = !isTimerPaused.value
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun saveGame(
        playerScore: Int,
        aiScore: Int,
        winner: String?,
        winnerScore: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                val result = gameRepository.saveGame(userId, gameState.value, playerScore, aiScore, winner, winnerScore)
                if (result.isSuccess) {
                    onSuccess()
                } else {
                    onError(result.exceptionOrNull()?.message ?: "Error saving game")
                }
            } else {
                onError("User not authenticated!")
            }
        }
    }

    private fun placeShips(board: Array<Array<CellState>>, ships: List<Ship>) {
        ships.forEach { ship ->
            var placed = false
            while (!placed) {
                val orientation = listOf(0, 1).random()
                val row = (0..9).random()
                val col = (0..9).random()

                if (canPlaceShip(board, row, col, ship.size, orientation)) {
                    ship.startRow = row
                    ship.startCol = col
                    ship.orientation = orientation

                    for (i in 0 until ship.size) {
                        if (orientation == 0) {
                            board[row][col + i] = CellState.SHIP
                        } else {
                            board[row + i][col] = CellState.SHIP
                        }
                    }
                    placed = true
                }
            }
        }
    }

    private fun canPlaceShip(board: Array<Array<CellState>>, row: Int, col: Int, size: Int, orientation: Int): Boolean {
        if (orientation == 0 && col + size > 10) return false
        if (orientation == 1 && row + size > 10) return false
        for (i in 0 until size) {
            val r = if (orientation == 0) row else row + i
            val c = if (orientation == 0) col + i else col
            if (board[r][c] != CellState.EMPTY) return false
        }
        return true
    }

    private fun placeMines(board: Array<Array<CellState>>, numberOfMines: Int): List<Pair<Int, Int>> {
        val minePositions = mutableListOf<Pair<Int, Int>>()
        var minesPlaced = 0
        val maxMines = minOf(numberOfMines, 10)

        while (minesPlaced < maxMines) {
            val row = (0..9).random()
            val col = (0..9).random()

            if (board[row][col] == CellState.EMPTY) {
                board[row][col] = CellState.MINE
                minePositions.add(row to col)
                minesPlaced++
            }
        }
        return minePositions
    }

    fun aiTurn(): Pair<Int, Int> {
        while (true) {
            val row = (0..9).random()
            val col = (0..9).random()
            if (gameState.value.board[row][col] == CellState.EMPTY ||
                gameState.value.board[row][col] == CellState.SHIP ||
                gameState.value.board[row][col] == CellState.MINE) {
                return row to col
            }
        }
    }
}