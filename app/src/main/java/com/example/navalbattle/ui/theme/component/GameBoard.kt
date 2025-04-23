package com.naval.battle.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.compose.ui.layout.ContentScale
import com.example.navalbattle.data.model.CellState
import com.example.navalbattle.data.model.GameState
import com.example.navalbattle.R

@Composable
fun GameBoard(
    gameState: GameState,
    cellSize: Dp,
    isLightTheme: Boolean,
    gameOver: Boolean,
    onCellClick: (Int, Int) -> Unit
) {
    val columnLabels = ('A'..'J').toList()

    // Definir cores animadas
    val backgroundColor by animateColorAsState(
        targetValue = if (isLightTheme) Color(0xFFF5F5F5) else Color(0xFF42A5F5),
        animationSpec = tween(durationMillis = 500)
    )
    val labelColor by animateColorAsState(
        targetValue = if (isLightTheme) Color.Black else Color.White,
        animationSpec = tween(durationMillis = 500)
    )
    val emptyCellColor by animateColorAsState(
        targetValue = if (isLightTheme) Color(0xFFD3D3D3) else Color(0xFF42A5F5),
        animationSpec = tween(durationMillis = 500)
    )
    val missCellColor by animateColorAsState(
        targetValue = if (isLightTheme) Color.White else Color(0xFF4FC3F7),
        animationSpec = tween(durationMillis = 500)
    )

    Column(
        modifier = Modifier
            .background(backgroundColor)
            .padding(4.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(cellSize)
            )
            for (col in gameState.board[0].indices) {
                Text(
                    text = columnLabels[col].toString(),
                    color = labelColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .size(cellSize)
                        .wrapContentHeight()
                )
            }
        }

        Row {
            Column {
                for (row in gameState.board.indices) {
                    Text(
                        text = (row + 1).toString(),
                        color = labelColor,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(cellSize)
                            .wrapContentWidth()
                    )
                }
            }

            Box(
                modifier = Modifier
                    .border(1.dp, Color.Black)
            ) {
                Column {
                    for (row in gameState.board.indices) {
                        Row {
                            for (col in gameState.board[row].indices) {
                                val cellState = gameState.board[row][col]
                                val shipAtCell = gameState.ships.find { ship ->
                                    val startRow = ship.startRow ?: return@find false
                                    val startCol = ship.startCol ?: return@find false
                                    val orientation = ship.orientation ?: return@find false
                                    val size = ship.size

                                    if (orientation == 0) {
                                        row == startRow && col >= startCol && col < startCol + size
                                    } else {
                                        col == startCol && row >= startRow && row < startRow + size
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .size(cellSize)
                                        .border(0.5.dp, Color.Black)
                                        .background(
                                            when {
                                                cellState == CellState.HIT && shipAtCell?.isSunk == true -> {
                                                    if (shipAtCell.horizontalImageResId == null && shipAtCell.verticalImageResId == null) {
                                                        Color.Blue
                                                    } else {
                                                        Color.Transparent
                                                    }
                                                }
                                                cellState == CellState.HIT -> Color.Red
                                                cellState == CellState.MISS -> missCellColor // Usar cor animada
                                                else -> emptyCellColor // Usar cor animada
                                            }
                                        )
                                        .clickable { onCellClick(row, col) }
                                )
                            }
                        }
                    }
                }

                // Sobrepor a imagem dos navios afundados
                gameState.ships.forEach { ship ->
                    if ((ship.horizontalImageResId != null || ship.verticalImageResId != null) && ship.isSunk) {
                        val startRow = ship.startRow ?: return@forEach
                        val startCol = ship.startCol ?: return@forEach
                        val orientation = ship.orientation ?: return@forEach
                        val size = ship.size

                        val imageResId = if (orientation == 0) ship.horizontalImageResId else ship.verticalImageResId
                        if (imageResId == null) return@forEach

                        val imageWidth = if (orientation == 0) cellSize * size else cellSize
                        val imageHeight = if (orientation == 0) cellSize else cellSize * size

                        val offsetX = (startCol * cellSize.value).dp
                        val offsetY = (startRow * cellSize.value).dp

                        Image(
                            painter = painterResource(id = imageResId),
                            contentDescription = ship.name,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .size(width = imageWidth, height = imageHeight)
                                .zIndex(1f)
                        )
                    }
                }

                // Sobrepor a imagem das minas quando o jogo terminar
                if (gameOver) {
                    gameState.mines.forEach { (row, col) ->
                        val offsetX = (col * cellSize.value).dp
                        val offsetY = (row * cellSize.value).dp

                        Image(
                            painter = painterResource(id = R.drawable.mine),
                            contentDescription = "Mine",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .offset(x = offsetX, y = offsetY)
                                .size(cellSize)
                                .zIndex(1f)
                        )
                    }
                }
            }
        }
    }
}