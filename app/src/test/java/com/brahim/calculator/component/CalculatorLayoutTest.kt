package com.brahim.calculator.component

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorLayoutTest {

  private fun displayHeight(available: Dp, scientific: Boolean) =
      available - calculatorChromeHeight(scientific) - keypadHeightFor(available, scientific)

  @Test
  fun collapsedKeypadUsesItsIdealHeight() {
    assertEquals(330.dp, keypadHeightFor(700.dp, scientific = false))
  }

  @Test
  fun scientificStripIsTwoRowsTall() {
    assertEquals(102.dp, scientificPanelHeight())
  }

  @Test
  fun displayKeepsItsRoomOnEveryPhoneHeight() {
    for (height in 600..1200) {
      for (scientific in listOf(false, true)) {
        val available = height.dp
        val fitsWithoutSqueezingTheKeypad =
            available >=
                calculatorChromeHeight(scientific) + MinKeypadHeight + MinDisplayHeight
        if (fitsWithoutSqueezingTheKeypad) {
          val display = displayHeight(available, scientific)
          assertTrue(
              "display of $display on a ${height}dp screen (scientific=$scientific)",
              display.value >= MinDisplayHeight.value,
          )
        }
      }
    }
  }

  @Test
  fun keypadNeverShrinksBelowItsMinimum() {
    for (height in 200..1200) {
      for (scientific in listOf(false, true)) {
        assertTrue(keypadHeightFor(height.dp, scientific).value >= MinKeypadHeight.value)
      }
    }
  }

  @Test
  fun scientificKeysCostLessThanAHundredDp() {
    assertTrue(scientificPanelHeight().value <= 110f)
  }

  @Test
  fun tallerScreensNeverGetASmallerKeypad() {
    var previous = 0f
    for (height in 200..1400) {
      val keypad = keypadHeightFor(height.dp, scientific = true).value
      assertTrue("keypad shrank at $height", keypad >= previous)
      previous = keypad
    }
  }
}
