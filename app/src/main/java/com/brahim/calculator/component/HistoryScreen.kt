package com.brahim.calculator.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brahim.calculator.R
import com.brahim.calculator.element.HistoryCard
import com.brahim.calculator.ui.theme.Motion
import com.brahim.calculator.util.formatHistoryDate
import com.brahim.calculator.viewModel.HistoryVM

@Composable
fun HistoryScreen(onReuse: (String) -> Unit, modifier: Modifier = Modifier) {
  val viewModel: HistoryVM = viewModel(factory = HistoryVM.Factory)
  val entries by viewModel.history.collectAsStateWithLifecycle()
  val locale = LocalConfiguration.current.locales[0]
  val newestFirst = remember(entries) { entries.asReversed() }
  var confirmClear by remember { mutableStateOf(false) }

  if (entries.isEmpty()) {
    EmptyHistory(modifier = modifier)
  } else {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 12.dp)) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
            text =
                pluralStringResource(R.plurals.history_count, entries.size, entries.size),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 8.dp),
        )
        FilledTonalButton(
            onClick = { confirmClear = true },
            shape = MaterialTheme.shapes.large,
        ) {
          Icon(
              imageVector = Icons.Outlined.DeleteSweep,
              contentDescription = null,
              modifier = Modifier.size(18.dp),
          )
          Spacer(Modifier.width(8.dp))
          Text(text = stringResource(R.string.clear))
        }
      }

      LazyColumn(
          modifier = Modifier.fillMaxSize(),
          contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
          verticalArrangement = Arrangement.spacedBy(Motion.KeyGap),
      ) {
        itemsIndexed(
            items = newestFirst,
            key = { _, item -> item.dateTime + item.title + item.content },
        ) { index, item ->
          val dismissState = rememberSwipeToDismissBoxState()
          SwipeToDismissBox(
              state = dismissState,
              enableDismissFromStartToEnd = false,
              onDismiss = { viewModel.deleteAt(entries.lastIndex - index) },
              backgroundContent = { DeleteBackground() },
              modifier = Modifier.animateItem(),
          ) {
            HistoryCard(
                expression = item.title,
                result = item.content,
                dateLabel = formatHistoryDate(item.dateTime, locale),
                onReuse = { onReuse(item.title) },
            )
          }
        }
      }
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
                viewModel.clear()
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
private fun DeleteBackground() {
  Box(
      modifier =
          Modifier.fillMaxSize()
              .clip(MaterialTheme.shapes.large)
              .background(MaterialTheme.colorScheme.errorContainer)
              .padding(end = 24.dp),
      contentAlignment = Alignment.CenterEnd,
  ) {
    Icon(
        imageVector = Icons.Outlined.Delete,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onErrorContainer,
    )
  }
}

@Composable
private fun EmptyHistory(modifier: Modifier = Modifier) {
  val visibleState = remember { MutableTransitionState(false).apply { targetState = true } }

  AnimatedVisibility(
      visibleState = visibleState,
      enter = fadeIn(tween(Motion.MEDIUM_2)) + scaleIn(initialScale = 0.9f),
  ) {
    Column(
        modifier = modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Surface(shape = CircleShape, color = MaterialTheme.colorScheme.surfaceContainerHigh) {
        Box(modifier = Modifier.size(96.dp), contentAlignment = Alignment.Center) {
          Icon(
              imageVector = Icons.Outlined.History,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(40.dp),
          )
        }
      }
      Spacer(Modifier.height(24.dp))
      Text(
          text = stringResource(R.string.history_empty),
          style = MaterialTheme.typography.titleLarge,
          color = MaterialTheme.colorScheme.onSurface,
      )
      Spacer(Modifier.height(8.dp))
      Text(
          text = stringResource(R.string.history_empty_hint),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          textAlign = TextAlign.Center,
      )
    }
  }
}
