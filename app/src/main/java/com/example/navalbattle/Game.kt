package com.example.navalbattle

data class GameState(
    val board: Array<Array<CellState>> = Array(10) { Array(10) { CellState.EMPTY } },
    val ships: List<Ship> = listOf(
        Ship(5, "Aircraft Carrier"),
        Ship(4, "Tanker"),
        Ship(3, "Destroyer"),
        Ship(3, "Submarine"),
        Ship(2, "Patrol Boat")
    ),
    val moves: MutableList<Move> = mutableListOf(),
    val playerTurn: Boolean = true,
    val lastSunkByPlayer: Boolean = false // Indica se o último navio afundado foi pelo jogador
)

data class Ship(val size: Int, val name: String, var isSunk: Boolean = false)

data class Move(val row: Int, val col: Int, val result: CellState, val isPlayerMove: Boolean)

enum class CellState {
    EMPTY, SHIP, HIT, MISS
}