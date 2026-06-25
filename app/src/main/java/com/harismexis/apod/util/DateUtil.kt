package com.harismexis.apod.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

val firstApodDate = requireNotNull(LocalDate.of(1995, 6, 16))
private val uiDateFormatter = DateTimeFormatter.ofPattern("MMMM d, yyyy")
private val apodDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun String.toApodDate(): String {
    return LocalDate.parse(this, uiDateFormatter)
        .format(apodDateFormatter)
}

fun LocalDate.toApodDate(): String = format(DateTimeFormatter.ISO_LOCAL_DATE)

fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneOffset.UTC)
        .toLocalDate()
}

fun LocalDate.toMillis(): Long {
    return atStartOfDay(ZoneOffset.UTC)
        .toInstant()
        .toEpochMilli()
}

fun String.toUiDate(): String {
    return LocalDate.parse(this, apodDateFormatter)
        .format(uiDateFormatter)
}

