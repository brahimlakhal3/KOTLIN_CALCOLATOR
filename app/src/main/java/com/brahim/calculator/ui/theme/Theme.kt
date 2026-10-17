package com.brahim.calculator.ui.theme

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color

enum class ThemeMode {
  AUTO,
  LIGHT,
  DARK;

  companion object {
    fun from(value: String?): ThemeMode = entries.firstOrNull { it.name == value } ?: AUTO
  }
}

@Composable
fun ThemeMode.isDark(): Boolean =
    when (this) {
      ThemeMode.LIGHT -> false
      ThemeMode.DARK -> true
      ThemeMode.AUTO -> isSystemInDarkTheme()
    }

private val LightColors = lightColorScheme(
    primary = Crimson40,
    onPrimary = Crimson100,
    primaryContainer = Crimson85,
    onPrimaryContainer = Crimson10,
    inversePrimary = Crimson80,
    secondary = Rose40,
    onSecondary = Rose100,
    secondaryContainer = Rose90,
    onSecondaryContainer = Rose10,
    tertiary = Plum40,
    onTertiary = Plum100,
    tertiaryContainer = Plum90,
    onTertiaryContainer = Plum10,
    error = Error40,
    onError = Error100,
    errorContainer = Error90,
    onErrorContainer = Error10,
    background = Neutral98,
    onBackground = Neutral10,
    surface = Neutral98,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
    scrim = Neutral0,
    inverseSurface = Neutral20,
    inverseOnSurface = Neutral95,
    surfaceDim = Neutral87,
    surfaceBright = Neutral98,
    surfaceContainerLowest = Neutral100,
    surfaceContainerLow = Neutral96,
    surfaceContainer = Neutral94,
    surfaceContainerHigh = Neutral92,
    surfaceContainerHighest = Neutral90,
)

private val DarkColors = darkColorScheme(
    primary = Crimson80,
    onPrimary = Crimson20,
    primaryContainer = Crimson30,
    onPrimaryContainer = Crimson90,
    inversePrimary = Crimson40,
    secondary = Rose80,
    onSecondary = Rose20,
    secondaryContainer = Rose30,
    onSecondaryContainer = Rose90,
    tertiary = Plum80,
    onTertiary = Plum20,
    tertiaryContainer = Plum30,
    onTertiaryContainer = Plum90,
    error = Error80,
    onError = Error20,
    errorContainer = Error30,
    onErrorContainer = Error90,
    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral6,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
    scrim = Neutral0,
    inverseSurface = Neutral90,
    inverseOnSurface = Neutral20,
    surfaceDim = Neutral6,
    surfaceBright = Neutral24,
    surfaceContainerLowest = Neutral4,
    surfaceContainerLow = Neutral10,
    surfaceContainer = Neutral12,
    surfaceContainerHigh = Neutral17,
    surfaceContainerHighest = Neutral22,
)

@Composable
private fun animatedColorScheme(dark: Boolean): ColorScheme {
  val base = if (dark) DarkColors else LightColors
  val spec = Motion.standard<Color>(Motion.MEDIUM_2)

  @Composable
  fun animate(color: Color): Color =
      animateColorAsState(targetValue = color, animationSpec = spec, label = "scheme").value

  return base.copy(
      primary = animate(base.primary),
      onPrimary = animate(base.onPrimary),
      primaryContainer = animate(base.primaryContainer),
      onPrimaryContainer = animate(base.onPrimaryContainer),
      inversePrimary = animate(base.inversePrimary),
      secondary = animate(base.secondary),
      onSecondary = animate(base.onSecondary),
      secondaryContainer = animate(base.secondaryContainer),
      onSecondaryContainer = animate(base.onSecondaryContainer),
      tertiary = animate(base.tertiary),
      onTertiary = animate(base.onTertiary),
      tertiaryContainer = animate(base.tertiaryContainer),
      onTertiaryContainer = animate(base.onTertiaryContainer),
      error = animate(base.error),
      onError = animate(base.onError),
      errorContainer = animate(base.errorContainer),
      onErrorContainer = animate(base.onErrorContainer),
      background = animate(base.background),
      onBackground = animate(base.onBackground),
      surface = animate(base.surface),
      onSurface = animate(base.onSurface),
      surfaceVariant = animate(base.surfaceVariant),
      onSurfaceVariant = animate(base.onSurfaceVariant),
      outline = animate(base.outline),
      outlineVariant = animate(base.outlineVariant),
      inverseSurface = animate(base.inverseSurface),
      inverseOnSurface = animate(base.inverseOnSurface),
      surfaceDim = animate(base.surfaceDim),
      surfaceBright = animate(base.surfaceBright),
      surfaceContainerLowest = animate(base.surfaceContainerLowest),
      surfaceContainerLow = animate(base.surfaceContainerLow),
      surfaceContainer = animate(base.surfaceContainer),
      surfaceContainerHigh = animate(base.surfaceContainerHigh),
      surfaceContainerHighest = animate(base.surfaceContainerHighest),
  )
}

@Composable
fun AppTheme(themeMode: ThemeMode = ThemeMode.AUTO, content: @Composable () -> Unit) {
  val dark = themeMode.isDark()
  MaterialTheme(
      colorScheme = animatedColorScheme(dark),
      typography = AppTypography,
      shapes = AppShapes,
      content = content,
  )
}
