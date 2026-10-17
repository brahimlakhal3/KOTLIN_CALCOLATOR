package com.brahim.calculator.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brahim.calculator.BuildConfig
import com.brahim.calculator.R
import com.brahim.calculator.ui.theme.ThemeMode
import com.brahim.calculator.viewModel.AppConfigVM
import com.brahim.calculator.viewModel.HistoryVM

@Composable
fun SettingsScreen(configViewModel: AppConfigVM, modifier: Modifier = Modifier) {
  val mode by configViewModel.mode.collectAsStateWithLifecycle()
  val language by configViewModel.language.collectAsStateWithLifecycle()
  val historyViewModel: HistoryVM = viewModel(factory = HistoryVM.Factory)
  val entries by historyViewModel.history.collectAsStateWithLifecycle()
  var confirmClear by remember { mutableStateOf(false) }

  Column(
      modifier =
          modifier
              .fillMaxSize()
              .verticalScroll(rememberScrollState())
              .padding(horizontal = 12.dp)
              .padding(bottom = 32.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    SettingsCard(
        icon = Icons.Outlined.Palette,
        title = stringResource(R.string.appearance),
    ) {
      val selected = ThemeMode.from(mode)
      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        ThemeMode.entries.forEachIndexed { index, entry ->
          SegmentedButton(
              selected = selected == entry,
              onClick = { configViewModel.setMode(entry.name) },
              shape =
                  SegmentedButtonDefaults.itemShape(
                      index = index,
                      count = ThemeMode.entries.size,
                  ),
              label = { Text(stringResource(entry.labelRes)) },
          )
        }
      }
    }

    SettingsCard(icon = Icons.Outlined.Language, title = stringResource(R.string.language)) {
      val languages = listOf(AppConfigVM.LANGUAGE_FRENCH, AppConfigVM.LANGUAGE_ENGLISH)
      val selectedIndex = languages.indexOf(language).coerceAtLeast(0)
      SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
        languages.forEachIndexed { index, code ->
          SegmentedButton(
              selected = selectedIndex == index,
              onClick = { configViewModel.setLanguage(code) },
              shape =
                  SegmentedButtonDefaults.itemShape(index = index, count = languages.size),
              label = { Text(stringResource(languageNameRes(code))) },
          )
        }
      }
    }

    SettingsCard(icon = Icons.Outlined.Storage, title = stringResource(R.string.data)) {
      ListItem(
          headlineContent = { Text(stringResource(R.string.history_title)) },
          supportingContent = {
            Text(pluralStringResource(R.plurals.history_count, entries.size, entries.size))
          },
          leadingContent = { Icon(Icons.Outlined.DeleteSweep, contentDescription = null) },
          trailingContent = {
            FilledTonalButton(
                onClick = { confirmClear = true },
                enabled = entries.isNotEmpty(),
            ) {
              Text(stringResource(R.string.clear))
            }
          },
          colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      )
    }

    SettingsCard(icon = Icons.Outlined.Info, title = stringResource(R.string.about)) {
      ListItem(
          headlineContent = { Text(stringResource(R.string.app_name)) },
          supportingContent = {
            Text(stringResource(R.string.app_version, BuildConfig.VERSION_NAME))
          },
          leadingContent = { Icon(Icons.Outlined.Calculate, contentDescription = null) },
          colors = ListItemDefaults.colors(containerColor = Color.Transparent),
      )
    }
  }

  if (confirmClear) {
    AlertDialog(
        onDismissRequest = { confirmClear = false },
        icon = { Icon(Icons.Outlined.DeleteSweep, contentDescription = null) },
        title = { Text(stringResource(R.string.clear_history_title)) },
        text = { Text(stringResource(R.string.clear_history_text)) },
        confirmButton = {
          TextButton(
              onClick = {
                historyViewModel.clear()
                confirmClear = false
              },
          ) {
            Text(stringResource(R.string.clear))
          }
        },
        dismissButton = {
          TextButton(onClick = { confirmClear = false }) { Text(stringResource(R.string.cancel)) }
        },
    )
  }
}

@Composable
private fun SettingsCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit,
) {
  Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.large,
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp),
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
        )
      }
      Spacer(Modifier.height(12.dp))
      content()
    }
  }
}

private val ThemeMode.labelRes: Int
  get() =
      when (this) {
        ThemeMode.AUTO -> R.string.theme_system
        ThemeMode.LIGHT -> R.string.theme_light
        ThemeMode.DARK -> R.string.theme_dark
      }

private fun languageNameRes(code: String): Int =
    when (code) {
      AppConfigVM.LANGUAGE_ENGLISH -> R.string.language_english
      else -> R.string.language_french
    }
