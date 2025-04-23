package com.example.navalbattle.data.model

data class GameState(
    val board: Array<Array<CellState>> = Array(10) { Array(10) { CellState.EMPTY } },
    val ships: List<Ship> = listOf(
        Ship(5, "Porta-aviões"),
        Ship(4, "Navio-tanque"),
        Ship(3, "Contratorpedeiro"),
        Ship(3, "Submarino"),
        Ship(2, "Destroyer")
    ),
    val moves: MutableList<Move> = mutableListOf(),  // Moves positions
    val mines: List<Pair<Int, Int>> = emptyList(),   // Mine positions
    val playerTurn: Boolean = true,                  // Turn
    val lastSunkByPlayer: Boolean = false            // Last hit
)