package com.brahim.calculator.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorTest {

  @Test
  fun evaluatesThePrettyOperators() {
    assertEquals("6", Calculator.evaluate("2×3").getOrThrow())
    assertEquals("3", Calculator.evaluate("9÷3").getOrThrow())
    assertEquals("1", Calculator.evaluate("4−3").getOrThrow())
    assertEquals("7", Calculator.evaluate("3+4").getOrThrow())
  }

  @Test
  fun hidesBinaryNoise() {
    assertEquals("0.3", Calculator.evaluate("0.1+0.2").getOrThrow())
    assertEquals("0.3333333333", Calculator.evaluate("1÷3").getOrThrow())
  }

  @Test
  fun evaluatesScientificKeys() {
    assertEquals("3", Calculator.evaluate("√(9)").getOrThrow())
    assertEquals("1024", Calculator.evaluate("2^10").getOrThrow())
    assertEquals("3", Calculator.evaluate("log10(1000)").getOrThrow())
    assertEquals("0", Calculator.evaluate("sin(0)").getOrThrow())
    assertEquals("0.25", Calculator.evaluate("2^(-2)").getOrThrow())
    assertEquals("2.7182818285", Calculator.evaluate("e").getOrThrow())
  }

  @Test
  fun evaluatesPercentAsDivisionByHundred() {
    assertEquals("0.5", Calculator.evaluate("50%").getOrThrow())
    assertEquals("10.5", Calculator.evaluate("50%+10").getOrThrow())
  }

  @Test
  fun failsInsteadOfThrowing() {
    assertTrue(Calculator.evaluate("").isFailure)
    assertTrue(Calculator.evaluate("2×").isFailure)
    assertTrue(Calculator.evaluate("1÷0").isFailure)
    assertTrue(Calculator.evaluate("2×÷3").isFailure)
    assertTrue(Calculator.evaluate("√()").isFailure)
  }

  @Test
  fun formatsPlainNumbersWithoutTrailingZeros() {
    assertEquals("0", Calculator.format(0.0))
    assertEquals("0", Calculator.format(-0.0))
    assertEquals("-0.5", Calculator.format(-0.5))
    assertEquals("100", Calculator.format(100.0))
    assertEquals("123456789012", Calculator.format(1.23456789012E11))
  }

  @Test
  fun switchesToScientificNotationAtTheExtremes() {
    assertEquals("1e13", Calculator.format(1E13))
    assertEquals("1.5e-07", Calculator.format(1.5E-7))
    assertEquals("1e-07", Calculator.format(1E-7))
  }

  @Test
  fun rejectsNotANumber() {
    assertTrue(runCatching { Calculator.format(Double.NaN) }.isFailure)
    assertTrue(runCatching { Calculator.format(Double.POSITIVE_INFINITY) }.isFailure)
  }

  @Test
  fun appendsAnImplicitTimesBeforeFunctionsAndConstants() {
    assertEquals("2×π", Calculator.append("2", "π"))
    assertEquals("2×e", Calculator.append("2", "e"))
    assertEquals("2×sin(", Calculator.append("2", "sin("))
    assertEquals("2×√(", Calculator.append("2", "√("))
    assertEquals("(2+3)×(", Calculator.append("(2+3)", "("))
  }

  @Test
  fun doesNotAppendATimesAfterAnOperator() {
    assertEquals("2+3", Calculator.append("2+", "3"))
    assertEquals("2+(", Calculator.append("2+", "("))
    assertEquals("2+π", Calculator.append("2+", "π"))
    assertEquals("2^2", Calculator.append("2", "^2"))
    assertEquals("7", Calculator.append("", "7"))
  }

  @Test
  fun bracketKeyClosesAnOpenBracketAroundAValue() {
    assertEquals("(", Calculator.parenthesis(""))
    assertEquals("2(", Calculator.parenthesis("2"))
    assertEquals("(2)", Calculator.parenthesis("(2"))
    assertEquals("(2)+(3)", Calculator.parenthesis("(2)+(3"))
    assertEquals("(2)×(", Calculator.parenthesis("(2)×"))
    assertEquals("√(9)", Calculator.parenthesis("√(9"))
  }

  @Test
  fun convertsPrettySyntaxForTheEngine() {
    assertEquals("2*3", Calculator.toMachineSyntax("2×3"))
    assertEquals("9/3", Calculator.toMachineSyntax("9÷3"))
    assertEquals("4-3", Calculator.toMachineSyntax("4−3"))
    assertEquals("sqrt(9)", Calculator.toMachineSyntax("√(9)"))
    assertEquals("pi", Calculator.toMachineSyntax("π"))
    assertEquals("2/100", Calculator.toMachineSyntax("2%"))
  }
}
