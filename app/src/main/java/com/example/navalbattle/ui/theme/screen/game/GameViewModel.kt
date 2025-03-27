package com.example.navalbattle.ui.theme.screen.game

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.navalbattle.data.model.CellState
import com.example.navalbattle.data.model.GameState
import com.example.navalbattle.data.model.Ship
import com.google.firebase.auth.FirebaseAuth
import com.naval.battle.data.repository.GameRepository
import kotlinx.coroutines.launch

class GameViewModel(private val gameRepository: GameRepository) : ViewModel() {
    val gameState = mutableStateOf(GameState())
    val showSaveDialog = mutableStateOf(false)

    init {
        resetGame()
    }

    fun resetGame() {
        gameState.value = GameState()
        placeShips(gameState.value.board, gameState.value.ships)
    }

    fun saveGame(playerScore: Int, aiScore: Int, winner: String?, winnerScore: Int, onSuccess: () -> Unit, onError: (String) -> Unit) {
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
                    for (i in 0 until ship.size) {
                        if (orientation == 0) board[row][col + i] = CellState.SHIP
                        else board[row + i][col] = CellState.SHIP
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

    fun aiTurn(): Pair<Int, Int> {
        while (true) {
            val row = (0..9).random()
            val col = (0..9).random()
            if (gameState.value.board[row][col] == CellState.EMPTY || gameState.value.board[row][col] == CellState.SHIP) {
                return row to col
            }
        }
    }
}