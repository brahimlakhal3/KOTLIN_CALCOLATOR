package com.brahim.calculator.element

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.brahim.calculator.ui.theme.Motion

@Composable
fun ContactButton(
    description: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by
      animateFloatAsState(
          targetValue = if (pressed) 0.86f else 1f,
          animationSpec = Motion.press,
          label = "contactPress",
      )
  val haptics = LocalHapticFeedback.current

  Surface(
      onClick = {
        haptics.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        onClick()
      },
      modifier =
          Modifier.size(46.dp)
              .graphicsLayer {
                scaleX = scale
                scaleY = scale
              }
              .semantics { contentDescription = description },
      shape = CircleShape,
      color = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      interactionSource = interactionSource,
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { icon() }
  }
}
