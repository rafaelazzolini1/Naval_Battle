package com.example.navalbattle.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape

/**
 * Composable function for a theme toggle button that switches between light and dark modes.
 *
 * @param isLightTheme Whether the light theme is currently active
 * @param onThemeToggle Callback to toggle between light and dark themes
 * @param modifier Modifier for styling and positioning the button
 */
@Composable
fun ThemeToggle(
    isLightTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onThemeToggle(!isLightTheme) },
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                color = if (isLightTheme)
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)
                else
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f)
            )
    ) {
        // Display light or dark mode icon based on current theme
        Icon(
            imageVector = if (isLightTheme) Icons.Filled.LightMode else Icons.Filled.NightlightRound,
            contentDescription = "Toggle theme",
            tint = if (isLightTheme)
                MaterialTheme.colorScheme.onPrimaryContainer
            else
                MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}