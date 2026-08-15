package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = BrandCyanPrimary,
  onPrimary = Color.White,
  primaryContainer = SlateDarkCardElevated,
  onPrimaryContainer = TextPrimaryDark,
  secondary = SuccessEmerald,
  onSecondary = Color.White,
  secondaryContainer = SuccessEmeraldDim.copy(alpha = 0.3f),
  onSecondaryContainer = SuccessEmeraldBright,
  tertiary = BrandIndigo,
  onTertiary = Color.White,
  error = DangerRose,
  onError = Color.White,
  background = DeepNavySlateDark,
  onBackground = TextPrimaryDark,
  surface = SlateDarkCard,
  onSurface = TextPrimaryDark,
  surfaceVariant = SlateDarkCardElevated,
  onSurfaceVariant = TextSecondaryDark,
  outline = SlateDarkBorder,
  outlineVariant = SlateDarkBorderHighlight
)

private val LightColorScheme = lightColorScheme(
  primary = BrandCyanPrimary,
  onPrimary = Color.White,
  primaryContainer = SlateLightCardElevated,
  onPrimaryContainer = TextPrimaryLight,
  secondary = SuccessEmerald,
  onSecondary = Color.White,
  secondaryContainer = SuccessEmerald.copy(alpha = 0.15f),
  onSecondaryContainer = SuccessEmeraldDim,
  tertiary = BrandIndigo,
  onTertiary = Color.White,
  error = DangerRose,
  onError = Color.White,
  background = DeepNavySlateLight,
  onBackground = TextPrimaryLight,
  surface = SlateLightCard,
  onSurface = TextPrimaryLight,
  surfaceVariant = SlateLightCardElevated,
  onSurfaceVariant = TextSecondaryLight,
  outline = SlateLightBorder,
  outlineVariant = SlateLightBorderHighlight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}


