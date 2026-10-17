package com.brahim.calculator.element

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brahim.calculator.R
import com.brahim.calculator.ui.theme.Motion
import androidx.compose.ui.res.stringResource

@Composable
fun CalcDisplay(
    expression: String,
    value: String,
    evaluated: Boolean,
    copied: Boolean,
    onCopy: () -> Unit,
    modifier: Modifier = Modifier,
) {
  Surface(
      onClick = onCopy,
      modifier = modifier.fillMaxSize().animateContentSize(animationSpec = Motion.standard()),
      shape = MaterialTheme.shapes.extraLarge,
      color = MaterialTheme.colorScheme.surfaceContainerLow,
      contentColor = MaterialTheme.colorScheme.onSurface,
  ) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.End,
    ) {
      AnimatedContent(
          targetState = expression.ifEmpty { "0" },
          transitionSpec = {
            fadeIn(tween(Motion.SHORT_2)) togetherWith fadeOut(tween(Motion.SHORT_1))
          },
          label = "expression",
      ) { text ->
        Text(
            text = text,
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth(),
        )
      }

      Spacer(Modifier.height(12.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
      Spacer(Modifier.height(12.dp))

      Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
        AnimatedContent(
            targetState = value,
            transitionSpec = {
              (slideInVertically(animationSpec = Motion.enter()) { it / 4 } +
                  fadeIn(tween(Motion.MEDIUM_1))) togetherWith
                  (slideOutVertically(animationSpec = Motion.exit()) { -it / 4 } +
                      fadeOut(tween(Motion.SHORT_1)))
            },
            label = "result",
        ) { result ->
          Text(
              text = result,
              style =
                  MaterialTheme.typography.displayMedium.copy(fontWeight = FontWeight.SemiBold),
              color =
                  if (evaluated) MaterialTheme.colorScheme.primary
                  else MaterialTheme.colorScheme.onSurfaceVariant,
              maxLines = 1,
              textAlign = TextAlign.End,
              autoSize =
                  TextAutoSize.StepBased(
                      minFontSize = 18.sp,
                      maxFontSize = 54.sp,
                      stepSize = 0.5.sp,
                  ),
              modifier = Modifier.fillMaxWidth(),
          )
        }
      }

      Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End,
          verticalAlignment = Alignment.CenterVertically,
      ) {
        AnimatedVisibility(
            visible = copied,
            enter = fadeIn(tween(Motion.SHORT_2)) + scaleIn(initialScale = 0.8f),
            exit = fadeOut(tween(Motion.MEDIUM_1)),
        ) {
          Surface(
              shape = MaterialTheme.shapes.small,
              color = MaterialTheme.colorScheme.inverseSurface,
              contentColor = MaterialTheme.colorScheme.inverseOnSurface,
          ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
              Icon(
                  imageVector = Icons.Outlined.Check,
                  contentDescription = null,
                  modifier = Modifier.size(16.dp),
              )
              Text(
                  text = stringResource(R.string.copied),
                  style = MaterialTheme.typography.labelMedium,
              )
              Spacer(Modifier.width(2.dp))
            }
          }
        }
      }
    }
  }
}
