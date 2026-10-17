package com.brahim.calculator.util

import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale
import net.objecthunter.exp4j.ExpressionBuilder

object Calculator {

  private const val SCIENTIFIC_THRESHOLD = 1E12
  private const val MIN_THRESHOLD = 1E-6

  private val replacements = listOf(
      "×" to "*",
      "÷" to "/",
      "−" to "-",
      "√" to "sqrt",
      "π" to "pi",
      "%" to "/100",
  )

  fun toMachineSyntax(expression: String): String =
      replacements.fold(expression) { acc, (pretty, machine) -> acc.replace(pretty, machine) }

  fun evaluate(expression: String): Result<String> = runCatching {
    require(expression.isNotBlank()) { "Empty expression" }
    val value = ExpressionBuilder(toMachineSyntax(expression)).build().evaluate()
    format(value)
  }

  fun append(expression: String, token: String): String {
    val last = expression.lastOrNull() ?: return expression + token
    val endsValue =
        last.isDigit() || last == ')' || last == '.' || last == 'π' || last == 'e'
    val startsValue =
        token.firstOrNull()?.let { it.isLetter() || it == '(' || it == '√' } == true
    return if (endsValue && startsValue) "$expression×$token" else expression + token
  }

  fun parenthesis(expression: String): String {
    val last = expression.lastOrNull()
    val endsWithValue =
        last != null && (last.isDigit() || last == ')' || last == '.' || last == 'π' || last == 'e')
    val unclosed = expression.count { it == '(' } - expression.count { it == ')' }
    return if (endsWithValue && unclosed > 0) "$expression)" else "$expression("
  }

  fun format(value: Double): String {
    if (value.isNaN() || value.isInfinite()) error("Not a number")
    if (value == 0.0) return "0"

    val magnitude = kotlin.math.abs(value)
    if (magnitude !in MIN_THRESHOLD..<SCIENTIFIC_THRESHOLD) {
      return String.format(Locale.US, "%.6e", value)
          .replace("e+", "e")
          .replace(Regex("0+e"), "e")
          .replace(Regex("\\.e"), "e")
    }

    val rounded =
        value.toBigDecimal(MathContext(12)).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros()
    return rounded.toPlainString()
  }

  fun BigDecimal.toCleanString(): String = stripTrailingZeros().toPlainString()
}
