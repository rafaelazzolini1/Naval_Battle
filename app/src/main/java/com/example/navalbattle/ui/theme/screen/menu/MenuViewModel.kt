package com.example.navalbattle.ui.theme.screen.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MenuViewModel : ViewModel() {
    var minesInput by mutableStateOf("5")
        private set

    var timerInput by mutableStateOf("7")
        private set

    fun updateMinesInput(newValue: String) {
        minesInput = newValue
    }

    fun updateTimerInput(newValue: String) {
        timerInput = newValue
    }
}