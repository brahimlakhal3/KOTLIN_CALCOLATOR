package com.brahim.calculator.component

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Backspace
import androidx.compose.material.icons.outlined.Functions
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.brahim.calculator.R
import com.brahim.calculator.element.CalcDisplay
import com.brahim.calculator.element.CalcIconAction
import com.brahim.calculator.element.CalcKey
import com.brahim.calculator.ui.theme.Motion
import com.brahim.calculator.util.Calculator
import com.brahim.calculator.viewModel.HistoryVM
import java.time.LocalDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

internal val ActionsRowHeight = 52.dp
private val ScientificKeyHeight = 46.dp
private const val ScientificColumns = 6

private val KeypadHeightCollapsed = 330.dp
private val KeypadHeightExpanded = 272.dp

internal val MinDisplayHeight = 168.dp
internal val MinKeypadHeight = 260.dp

internal fun scientificPanelHeight(): Dp {
  val rows = (scientificKeys.size + ScientificColumns - 1) / ScientificColumns
  return ScientificKeyHeight * rows + Motion.KeyGap * (rows - 1)
}

internal fun calculatorChromeHeight(scientific: Boolean): Dp =
    ActionsRowHeight + Motion.KeyGap * (if (scientific) 3 else 2) +
        (if (scientific) scientificPanelHeight() else 0.dp)

internal fun keypadHeightFor(availableHeight: Dp, scientific: Boolean): Dp {
  val ideal = if (scientific) KeypadHeightExpanded else KeypadHeightCollapsed
  return (availableHeight - calculatorChromeHeight(scientific) - MinDisplayHeight)
      .coerceIn(MinKeypadHeight, ideal)
}

@Composable
fun CalculatorScreen(
    onOpenHistory: () -> Unit,
    reusedExpression: String?,
    onReusedExpressionHandled: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val clipboard = LocalClipboard.current
  val scope = rememberCoroutineScope()
  val historyViewModel: HistoryVM = viewModel(factory = HistoryVM.Factory)

  var expression by rememberSaveable { mutableStateOf("") }
  var result by rememberSaveable { mutableStateOf<String?>(null) }
  var scientific by rememberSaveable { mutableStateOf(false) }
  var copied by remember { mutableStateOf(false) }

  LaunchedEffect(reusedExpression) {
    if (!reusedExpression.isNullOrBlank()) {
      expression = reusedExpression
      result = null
      onReusedExpressionHandled()
    }
  }

  val preview by produceState<String?>(initialValue = null, expression) {
    value = null
    if (expression.isBlank()) return@produceState
    delay(120.milliseconds)
    value = withContext(Dispatchers.Default) { Calculator.evaluate(expression).getOrNull() }
  }

  fun type(token: String) {
    val evaluated = result
    if (evaluated != null) {
      val continues = token.firstOrNull() in setOf('+', '−', '×', '÷', '%', '^')
      expression = if (continues) evaluated else ""
      result = null
    }
    expression = Calculator.append(expression, token)
  }

  fun clear() {
    expression = ""
    result = null
  }

  fun evaluate() {
    Calculator.evaluate(expression).onSuccess { value ->
      result = value
      historyViewModel.add(expression, value, LocalDateTime.now().toString())
    }
  }

  fun copyResult() {
    val value = result ?: preview ?: return
    scope.launch {
      clipboard.setClipEntry(ClipEntry(ClipData.newPlainText("result", value)))
      copied = true
      delay(1500.milliseconds)
      copied = false
    }
  }

  BoxWithConstraints(modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 8.dp)) {
    val keypadHeight by
        animateDpAsState(
            targetValue = keypadHeightFor(maxHeight, scientific),
            animationSpec = Motion.gentleDp,
            label = "keypadHeight",
        )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Motion.KeyGap),
    ) {
      CalcDisplay(
          expression = expression,
          value = result ?: preview ?: "0",
          evaluated = result != null,
          copied = copied,
          onCopy = { copyResult() },
          modifier = Modifier.fillMaxWidth().weight(1f),
      )

      Row(
          modifier = Modifier.fillMaxWidth().height(ActionsRowHeight),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        CalcIconAction(
            icon = Icons.Outlined.History,
            contentDescription = stringResource(R.string.history_description),
            onClick = onOpenHistory,
        )
        CalcIconAction(
            icon = Icons.AutoMirrored.Outlined.Backspace,
            contentDescription = stringResource(R.string.backspace_description),
            onClick = {
              expression = expression.dropLast(1)
              result = null
            },
        )
      }

      AnimatedVisibility(
          visible = scientific,
          enter =
              expandVertically(
                  animationSpec = Motion.gentleSpring<IntSize>(),
                  expandFrom = Alignment.Top,
              ) + fadeIn(animationSpec = tween(Motion.MEDIUM_1)),
          exit =
              shrinkVertically(
                  animationSpec = Motion.gentleSpring<IntSize>(),
                  shrinkTowards = Alignment.Top,
              ) + fadeOut(animationSpec = tween(Motion.SHORT_1)),
      ) {
        ScientificKeypad(onKey = { token -> type(token) })
      }

      Column(
          modifier = Modifier.fillMaxWidth().height(keypadHeight),
          verticalArrangement = Arrangement.spacedBy(Motion.KeyGap),
      ) {
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Motion.KeyGap),
        ) {
          FunctionKey(label = "AC", onClick = { clear() })
          FunctionKey(
              label = "( )",
              onClick = {
                expression = Calculator.parenthesis(expression)
                result = null
              },
          )
          OperatorKey(label = "%", onClick = { type("%") })
          OperatorKey(label = "÷", onClick = { type("÷") })
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Motion.KeyGap),
        ) {
          DigitKey(label = "7", onClick = { type("7") })
          DigitKey(label = "8", onClick = { type("8") })
          DigitKey(label = "9", onClick = { type("9") })
          OperatorKey(label = "×", onClick = { type("×") })
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Motion.KeyGap),
        ) {
          DigitKey(label = "4", onClick = { type("4") })
          DigitKey(label = "5", onClick = { type("5") })
          DigitKey(label = "6", onClick = { type("6") })
          OperatorKey(label = "−", onClick = { type("−") })
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Motion.KeyGap),
        ) {
          DigitKey(label = "1", onClick = { type("1") })
          DigitKey(label = "2", onClick = { type("2") })
          DigitKey(label = "3", onClick = { type("3") })
          OperatorKey(label = "+", onClick = { type("+") })
        }

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Motion.KeyGap),
        ) {
          val rotation by
              animateFloatAsState(
                  targetValue = if (scientific) 180f else 0f,
                  animationSpec = Motion.settle,
                  label = "scientificToggle",
              )
          FunctionKey(
              label = stringResource(R.string.scientific_description),
              icon = Icons.Outlined.Functions,
              iconRotation = rotation,
              onClick = { scientific = !scientific },
          )
          DigitKey(label = "0", onClick = { type("0") })
          DigitKey(label = ".", onClick = { type(".") })
          CalcKey(
              label = "=",
              onClick = { evaluate() },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              containerColor = MaterialTheme.colorScheme.primary,
              contentColor = MaterialTheme.colorScheme.onPrimary,
              textStyle = MaterialTheme.typography.headlineSmall,
          )
        }
      }
    }
  }
}

@Composable
private fun RowScope.DigitKey(label: String, onClick: () -> Unit) {
  CalcKey(
      label = label,
      onClick = onClick,
      modifier = Modifier.weight(1f).fillMaxHeight(),
      containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
      contentColor = MaterialTheme.colorScheme.onSurface,
  )
}

@Composable
private fun RowScope.OperatorKey(label: String, onClick: () -> Unit) {
  CalcKey(
      label = label,
      onClick = onClick,
      modifier = Modifier.weight(1f).fillMaxHeight(),
      containerColor = MaterialTheme.colorScheme.primaryContainer,
      contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
  )
}

@Composable
private fun RowScope.FunctionKey(
    label: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
) {
  CalcKey(
      label = label,
      onClick = onClick,
      modifier = Modifier.weight(1f).fillMaxHeight(),
      containerColor = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      icon = icon,
      iconRotation = iconRotation,
  )
}

private data class ScientificEntry(val label: String, val token: String)

private val scientificKeys =
    listOf(
        ScientificEntry("sin", "sin("),
        ScientificEntry("cos", "cos("),
        ScientificEntry("tan", "tan("),
        ScientificEntry("ln", "log("),
        ScientificEntry("log", "log10("),
        ScientificEntry("√", "√("),
        ScientificEntry("x²", "^2"),
        ScientificEntry("xʸ", "^("),
        ScientificEntry("π", "π"),
        ScientificEntry("e", "e"),
        ScientificEntry("1/x", "^(-1)"),
        ScientificEntry("eˣ", "e^("),
    )

@Composable
private fun ScientificKeypad(onKey: (String) -> Unit) {
  Column(
      modifier = Modifier.fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(Motion.KeyGap),
  ) {
    scientificKeys.chunked(ScientificColumns).forEach { row ->
      Row(
          modifier = Modifier.fillMaxWidth().height(ScientificKeyHeight),
          horizontalArrangement = Arrangement.spacedBy(Motion.KeyGap),
      ) {
        row.forEach { entry ->
          CalcKey(
              label = entry.label,
              onClick = { onKey(entry.token) },
              modifier = Modifier.weight(1f).fillMaxHeight(),
              containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
              contentColor = MaterialTheme.colorScheme.onSurface,
              textStyle = MaterialTheme.typography.titleSmall,
          )
        }
      }
    }
  }
}
