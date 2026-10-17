package com.brahim.calculator.util

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private val formatter: DateTimeFormatter =
    DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT)

fun formatHistoryDate(isoDateTime: String, locale: Locale): String = runCatching {
      LocalDateTime.parse(isoDateTime).format(formatter.withLocale(locale))
    }
    .getOrDefault(isoDateTime)
