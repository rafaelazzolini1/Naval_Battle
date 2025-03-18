package com.example.navalbattle

data class GameState(
    val board: Array<Array<CellState>> = Array(10) { Array(10) { CellState.EMPTY } },
    val ships: List<Ship> = listOf(
        Ship(5, "Porta-aviões"),
        Ship(4, "Navio-tanque"),
        Ship(3, "Contratorpedeiro"),
        Ship(3, "Submarino"),
        Ship(2, "Destroyer")
    ),
    val playerTurn: Boolean = true // true = jogador, false = IA
)

data class Ship(val size: Int, val name: String, var isSunk: Boolean = false)

enum class CellState {
    EMPTY, SHIP, HIT, MISS
}