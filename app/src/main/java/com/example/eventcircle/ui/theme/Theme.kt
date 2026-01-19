package com.example.eventcircle.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Teal200,
    background = Color.White,
    surface = Color.White,
    error = Color(0xFFB00020)
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue700,
    secondary = Teal700,
    background = Color(0xFF121212),
    surface = Color(0xFF121212),
    error = Color(0xFFCF6679)
)

@Composable
fun EventCircleTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}