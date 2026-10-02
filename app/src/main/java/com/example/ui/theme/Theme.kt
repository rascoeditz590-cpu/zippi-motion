package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val ZippiDarkColorScheme = darkColorScheme(
    primary = ZippiPink,
    onPrimary = Color.White,
    primaryContainer = ZippiPinkContainer,
    onPrimaryContainer = ZippiOnPinkContainer,
    secondary = ZippiAmber,
    onSecondary = Color.Black,
    secondaryContainer = ZippiAmberContainer,
    onSecondaryContainer = ZippiOnAmberContainer,
    tertiary = ZippiCoral,
    onTertiary = Color.White,
    background = StudioBackground,
    onBackground = TextPrimary,
    surface = StudioSurface,
    onSurface = TextPrimary,
    surfaceVariant = StudioSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = StudioCardBorder,
    outlineVariant = StudioDivider,
    inverseSurface = TextPrimary,
    inverseOnSurface = StudioBackground,
    error = Color(0xFFFF5252),
    onError = Color.White
)

@Composable
fun ZippiMotionTheme(
    content: @Composable () -> Unit
) {
    // Studio video and motion graphics editing demands a consistent, calibrated dark canvas
    MaterialTheme(
        colorScheme = ZippiDarkColorScheme,
        typography = Typography,
        content = content
    )
}
