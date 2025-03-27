package com.example.navalbattle.data.model

data class Ship(
    val size: Int,
    val name: String,
    var isSunk: Boolean = false
)