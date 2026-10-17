package com.brahim.calculator.element

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.brahim.calculator.ui.theme.Motion

@Composable
fun CalcKey(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerHighest,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.titleLarge,
    icon: ImageVector? = null,
    iconRotation: Float = 0f,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by
      animateFloatAsState(
          targetValue = if (pressed) 0.9f else 1f,
          animationSpec = Motion.press,
          label = "keyPress",
      )
  val haptics = LocalHapticFeedback.current

  Surface(
      onClick = {
        haptics.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        onClick()
      },
      modifier =
          modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
          },
      shape = MaterialTheme.shapes.large,
      color = containerColor,
      contentColor = contentColor,
      interactionSource = interactionSource,
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      if (icon != null) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier =
                Modifier.size(24.dp).graphicsLayer {
                  rotationZ = iconRotation
                },
        )
      } else {
        Text(text = label, style = textStyle, maxLines = 1)
      }
    }
  }
}

@Composable
fun CalcIconAction(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
  val interactionSource = remember { MutableInteractionSource() }
  val pressed by interactionSource.collectIsPressedAsState()
  val scale by
      animateFloatAsState(
          targetValue = if (pressed) 0.88f else 1f,
          animationSpec = Motion.press,
          label = "actionPress",
      )
  val haptics = LocalHapticFeedback.current

  Surface(
      onClick = {
        haptics.performHapticFeedback(HapticFeedbackType.KeyboardTap)
        onClick()
      },
      modifier =
          modifier.size(52.dp).graphicsLayer {
            scaleX = scale
            scaleY = scale
          },
      shape = MaterialTheme.shapes.extraLarge,
      color = MaterialTheme.colorScheme.secondaryContainer,
      contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
      interactionSource = interactionSource,
  ) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      Icon(
          imageVector = icon,
          contentDescription = contentDescription,
          modifier = Modifier.size(24.dp),
      )
    }
  }
}
