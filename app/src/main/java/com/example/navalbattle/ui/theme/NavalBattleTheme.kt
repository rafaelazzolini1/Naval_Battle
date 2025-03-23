package com.example.navalbattle.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Paleta de cores para o tema claro
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF90A4AE), // Cinza-azulado claro para botões
    onPrimary = Color.Black, // Texto preto sobre botões
    surface = Color(0xFFF5F5F5), // Fundo claro para campos
    onSurface = Color.Black, // Texto preto sobre fundo claro
    error = Color.Red, // Cor de erro
    background = Color.White // Fundo branco
)

// Paleta de cores para o tema escuro
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF0288D1), // Azul vibrante para botões
    onPrimary = Color.White, // Texto branco sobre botões
    surface = Color(0xFF1E3A8A), // Fundo azul escuro para campos
    onSurface = Color.White, // Texto branco sobre fundo escuro
    error = Color.Red, // Cor de erro
    background = Color(0xFF003087) // Fundo azul escuro
)

// Função que define o tema do aplicativo com base no estado isLightTheme
@Composable
fun NavalBattleTheme(isLightTheme: Boolean, content: @Composable () -> Unit) {
    val colors = if (isLightTheme) LightColorScheme else DarkColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography, // Usa a tipografia padrão do MaterialTheme
        content = content
    )
}