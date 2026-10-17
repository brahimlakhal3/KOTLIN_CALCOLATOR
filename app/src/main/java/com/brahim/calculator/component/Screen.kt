package com.brahim.calculator.component

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.brahim.calculator.R

enum class Screen(
    val route: String,
    @param:StringRes val titleRes: Int,
    val icon: ImageVector,
) {
  Calculator("calculator", R.string.screen_calculator, Icons.Outlined.Calculate),
  History("history", R.string.screen_history, Icons.Outlined.History),
  Settings("settings", R.string.screen_settings, Icons.Outlined.Settings);

  companion object {
    fun fromRoute(route: String?): Screen = entries.firstOrNull { it.route == route } ?: Calculator
  }
}
