package com.brahim.calculator

import android.app.Activity
import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.activity.compose.LocalActivity
import com.brahim.calculator.component.AppShell
import com.brahim.calculator.ui.theme.AppTheme
import com.brahim.calculator.ui.theme.ThemeMode
import com.brahim.calculator.ui.theme.isDark
import com.brahim.calculator.viewModel.AppConfigVM
import java.util.Locale

@Composable
fun MainScreen() {
  val activity = LocalActivity.current
  val configViewModel: AppConfigVM = viewModel(factory = AppConfigVM.Factory)
  val language by configViewModel.language.collectAsStateWithLifecycle()
  val mode by configViewModel.mode.collectAsStateWithLifecycle()
  val themeMode = ThemeMode.from(mode)

  SystemBars(darkTheme = themeMode.isDark(), activity = activity)

  AppTheme(themeMode = themeMode) {
    ProvideAppLocale(language) {
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        AppShell(configViewModel = configViewModel)
      }
    }
  }
}

@Composable
private fun SystemBars(darkTheme: Boolean, activity: Activity?) {
  val view = LocalView.current
  SideEffect {
    val window = activity?.window ?: return@SideEffect
    val controller = WindowInsetsControllerCompat(window, view)
    controller.isAppearanceLightStatusBars = !darkTheme
    controller.isAppearanceLightNavigationBars = !darkTheme
  }
}

@Composable
private fun ProvideAppLocale(language: String, content: @Composable () -> Unit) {
  val context = LocalContext.current
  val currentConfiguration = LocalConfiguration.current
  val localized = remember(language, context, currentConfiguration) {
    val configuration = Configuration(currentConfiguration)
    if (language.isNotBlank()) configuration.setLocale(Locale.forLanguageTag(language))
    configuration to context.createConfigurationContext(configuration)
  }
  CompositionLocalProvider(
      LocalConfiguration provides localized.first,
      LocalContext provides localized.second,
      LocalResources provides localized.second.resources,
      content = content,
  )
}
