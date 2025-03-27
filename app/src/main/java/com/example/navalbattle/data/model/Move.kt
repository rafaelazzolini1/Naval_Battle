package com.example.navalbattle.data.model

data class Move(
    val row: Int,
    val col: Int,
    val result: CellState,
    val isPlayerMove: Boolean
)