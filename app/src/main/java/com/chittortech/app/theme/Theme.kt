package com.chittortech.app.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary             = CtPrimaryBlue,
    onPrimary           = Color.White,
    primaryContainer    = CtPrimaryLight,
    onPrimaryContainer  = CtPrimaryDark,
    secondary           = CtGreen,
    onSecondary         = Color.White,
    secondaryContainer  = CtGreenLight,
    onSecondaryContainer = CtGreen,
    tertiary            = CtAmber,
    onTertiary          = Color.White,
    tertiaryContainer   = CtAmberLight,
    error               = CtRed,
    onError             = Color.White,
    errorContainer      = CtRedLight,
    background          = CtBackground,
    onBackground        = TextPrimary,
    surface             = CtCardWhite,
    onSurface           = TextPrimary,
    surfaceVariant      = CtSurfaceVariant,
    onSurfaceVariant    = TextSecondary,
    outline             = CtBorder,
    outlineVariant      = CtBorder
)

@Composable
fun ChittorTechTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography  = AppTypography,
        content     = content
    )
}
