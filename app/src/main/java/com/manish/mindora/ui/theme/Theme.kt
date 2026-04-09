package com.manish.mindora.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.core.view.WindowCompat

private val MindoraTertiary = Color(0xFF7E6BC4)
private val MindoraOnTertiary = Color(0xFFFFFFFF)
private val MindoraTertiaryContainer = Color(0xFFE8E3F5)
private val MindoraOnTertiaryContainer = Color(0xFF2D2545)

private val MindoraLightColorScheme = lightColorScheme(
    primary = MindoraPalette.MintGreen,
    onPrimary = MindoraPalette.TextPrimary,
    primaryContainer = MindoraPalette.PastelGreen,
    onPrimaryContainer = MindoraPalette.TextPrimary,
    secondary = MindoraPalette.SkyBlue,
    onSecondary = MindoraPalette.TextPrimary,
    secondaryContainer = MindoraPalette.BabyBlue,
    onSecondaryContainer = MindoraPalette.TextPrimary,
    tertiary = MindoraTertiary,
    onTertiary = MindoraOnTertiary,
    tertiaryContainer = MindoraTertiaryContainer,
    onTertiaryContainer = MindoraOnTertiaryContainer,
    background = MindoraPalette.BackgroundGreenWhite,
    onBackground = MindoraPalette.TextPrimary,
    surface = MindoraPalette.BackgroundGreenWhite,
    onSurface = MindoraPalette.TextPrimary,
    surfaceVariant = MindoraPalette.SurfaceBluishWhite,
    onSurfaceVariant = MindoraPalette.TextSecondary,
    outline = MindoraPalette.Divider,
    outlineVariant = MindoraPalette.Divider.copy(alpha = 0.55f),
)

private val MindoraDarkColorScheme = darkColorScheme(
    primary = MindoraPalette.PastelGreen,
    onPrimary = Color(0xFF0D1F1C),
    primaryContainer = Color(0xFF2D4A42),
    onPrimaryContainer = MindoraPalette.PastelGreen,
    secondary = MindoraPalette.BabyBlue,
    onSecondary = Color(0xFF0D1F1C),
    secondaryContainer = Color(0xFF2A4555),
    onSecondaryContainer = MindoraPalette.BabyBlue,
    tertiary = Color(0xFFB8A8E8),
    onTertiary = Color(0xFF1A1428),
    tertiaryContainer = Color(0xFF3D3558),
    onTertiaryContainer = Color(0xFFE8E3F5),
    background = Color(0xFF121A1A),
    onBackground = Color(0xFFE8F0EF),
    surface = Color(0xFF121A1A),
    onSurface = Color(0xFFE8F0EF),
    surfaceVariant = Color(0xFF1E2C2C),
    onSurfaceVariant = Color(0xFFB0C4C4),
    outline = Color(0xFF3D4F4F),
    outlineVariant = Color(0xFF2A3838),
)

@Composable
fun MindoraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    /** Brand palette is fixed; set true to use Android 12+ dynamic wallpaper colors instead. */
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MindoraDarkColorScheme
        else -> MindoraLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
