package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SkyBlueLight,
    onPrimary = DeepNavyDark,
    primaryContainer = DeepNavySurface,
    onPrimaryContainer = SkyBlueLight,
    secondary = EmeraldLight,
    onSecondary = DeepNavyDark,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = EmeraldLight,
    tertiary = AmberAccent,
    background = DarkBg,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkDivider
)

private val LightColorScheme = lightColorScheme(
    primary = SkyBluePrimary,
    onPrimary = Color.White,
    primaryContainer = SkyBlueContainer,
    onPrimaryContainer = OnSkyBlueContainer,
    secondary = EmeraldAccent,
    onSecondary = Color.White,
    secondaryContainer = EmeraldContainer,
    onSecondaryContainer = OnEmeraldContainer,
    tertiary = AmberAccent,
    background = OffWhiteBg,
    onBackground = TextPrimaryLight,
    surface = CardSurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = DividerLight
)

@Composable
fun TripMateTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Preserve our handcrafted sky-blue travel aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

// Alias for backwards compatibility
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    TripMateTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}
