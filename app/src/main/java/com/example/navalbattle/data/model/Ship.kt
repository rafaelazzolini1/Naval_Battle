package com.example.navalbattle.data.model

data class Ship(
    val size: Int,
    val name: String,
    var isSunk: Boolean = false,
    val horizontalImageResId: Int? = null,
    val verticalImageResId: Int? = null,
    var startRow: Int? = null,
    var startCol: Int? = null,
    var orientation: Int? = null
)