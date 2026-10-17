package com.brahim.calculator

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

  @get:Rule val rule = createAndroidComposeRule<MainActivity>()

  @Test
  fun keypadAndDisplayAreShown() {
    rule.onNodeWithText("AC").assertIsDisplayed()
    rule.onNodeWithText("=").assertIsDisplayed()
    rule.onNodeWithText("0").assertIsDisplayed()
  }

  @Test
  fun typingShowsTheExpressionAndItsResult() {
    rule.onNodeWithText("5").performClick()
    rule.onNodeWithText("+").performClick()
    rule.onNodeWithText("5").performClick()

    rule.onNodeWithText("5+5").assertIsDisplayed()

    rule.onNodeWithText("=").performClick()
    rule.onNodeWithText("10").assertIsDisplayed()
  }
}
