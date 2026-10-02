package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val NothingDarkColorScheme = darkColorScheme(
  primary = NothingWhite,
  onPrimary = NothingBlack,
  primaryContainer = NothingSurfaceElevated,
  onPrimaryContainer = NothingWhite,
  secondary = NothingRed,
  onSecondary = NothingWhite,
  secondaryContainer = NothingRedDim,
  onSecondaryContainer = NothingRedBright,
  tertiary = NothingTextSecondary,
  onTertiary = NothingWhite,
  background = NothingBlack,
  onBackground = NothingTextPrimary,
  surface = NothingSurface,
  onSurface = NothingTextPrimary,
  surfaceVariant = NothingSurfaceElevated,
  onSurfaceVariant = NothingTextSecondary,
  outline = NothingBorder,
  outlineVariant = NothingBorderSubtle
)

private val NothingLightColorScheme = lightColorScheme(
  primary = NothingBlack,
  onPrimary = NothingWhite,
  primaryContainer = NothingTextPrimary,
  onPrimaryContainer = NothingBlack,
  secondary = NothingRed,
  onSecondary = NothingWhite,
  secondaryContainer = NothingRedDim,
  onSecondaryContainer = NothingRedBright,
  tertiary = NothingTextTertiary,
  onTertiary = NothingBlack,
  background = NothingWhite,
  onBackground = NothingBlack,
  surface = NothingTextPrimary,
  onSurface = NothingBlack,
  surfaceVariant = NothingSurfaceHover,
  onSurfaceVariant = NothingTextSecondary,
  outline = NothingBorder,
  outlineVariant = NothingBorderSubtle
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to iconic Nothing dark mode
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) NothingDarkColorScheme else NothingLightColorScheme
  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
