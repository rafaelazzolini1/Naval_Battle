package com.example.navalbattle

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    var gameState = mutableStateOf(GameState())
    var showSaveDialog = mutableStateOf(false)

    init {
        // Posiciona os navios da IA ao iniciar
        placeShips(gameState.value.board, gameState.value.ships)
    }

    // Método para reiniciar o jogo
    fun resetGame() {
        gameState.value = GameState() // Cria um novo estado de jogo
        placeShips(gameState.value.board, gameState.value.ships) // Reposiciona os navios
        showSaveDialog.value = false // Fecha o diálogo de salvar, se estiver aberto
    }
}