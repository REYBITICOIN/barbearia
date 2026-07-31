package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

fun parseHexColor(hex: String?, fallback: Color): Color {
    if (hex.isNullOrBlank()) return fallback
    return try {
        val cleanHex = hex.removePrefix("#")
        Color(android.graphics.Color.parseColor("#$cleanHex"))
    } catch (e: Exception) {
        fallback
    }
}

private val DarkColorScheme = darkColorScheme(
    primary = BarberGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF332B0F),
    onPrimaryContainer = BarberGoldLight,
    secondary = BarberAiCyan,
    onSecondary = Color.Black,
    background = BarberDarkCharcoal,
    onBackground = BarberTextWhite,
    surface = BarberDarkSurface,
    onSurface = BarberTextWhite,
    surfaceVariant = BarberDarkCard,
    onSurfaceVariant = BarberTextMuted
)

private val LightColorScheme = lightColorScheme(
    primary = BarberGold,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFFFFF4CC),
    onPrimaryContainer = Color(0xFF423300),
    secondary = BarberAiCyan,
    onSecondary = Color.Black,
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1C1B1F),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F1F5),
    onSurfaceVariant = Color(0xFF49454F)
)

@Composable
fun BarberLabTheme(
    darkTheme: Boolean = true, // Default to dark luxury barbershop theme
    primaryHexOverride: String? = null,
    content: @Composable () -> Unit
) {
    val baseScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val customPrimary = parseHexColor(primaryHexOverride, baseScheme.primary)
    
    val finalScheme = baseScheme.copy(
        primary = customPrimary
    )

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content
    )
}

