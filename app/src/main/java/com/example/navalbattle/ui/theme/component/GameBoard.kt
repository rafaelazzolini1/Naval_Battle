package com.naval.battle.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.navalbattle.data.model.CellState
import com.example.navalbattle.data.model.GameState


@Composable
fun GameBoard(
    gameState: GameState,
    cellSize: Dp,
    isLightTheme: Boolean,
    onCellClick: (Int, Int) -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    colors = if (isLightTheme) listOf(Color(0xFFCFD8DC), Color(0xFFE0E0E0))
                    else listOf(Color(0xFF4FC3F7), Color(0xFF0288D1))
                )
            )
            .padding(8.dp)
    ) {
        for (i in 0 until 10) {
            Row {
                for (j in 0 until 10) {
                    Box(
                        modifier = Modifier
                            .size(cellSize)
                            .border(1.dp, if (isLightTheme) Color(0xFFB0BEC5) else Color.White.copy(alpha = 0.3f))
                            .background(
                                when (gameState.board[i][j]) {
                                    CellState.HIT -> Color(0xFFFF6F61).copy(alpha = 0.9f)
                                    CellState.MISS -> Color(0xFFB0BEC5).copy(alpha = 0.9f)
                                    else -> if (isLightTheme) Color(0xFFCFD8DC).copy(alpha = 0.9f)
                                    else Color(0xFFBBDEFB).copy(alpha = 0.9f)
                                }
                            )
                            .clickable { onCellClick(i, j) }
                    ) {
                        when (gameState.board[i][j]) {
                            CellState.HIT -> Text(
                                "X",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            CellState.MISS -> Text(
                                "O",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.align(Alignment.Center)
                            )
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}