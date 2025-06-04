package com.harismexis.apod.util

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val APOD_DATE_FORMAT = "yyyy-MM-dd"

fun convertMillisToApodDate(millis: Long): String {
    val stringDate = convertMillisToDate(millis)
    val calendar = getCalendarFromDate(stringDate)
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    return getApodFormattedDate(year, month, day)
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat(APOD_DATE_FORMAT, Locale.getDefault())
    return formatter.format(Date(millis))
}

fun getCalendarFromDate(date: String): Calendar {
    val calendar: Calendar = Calendar.getInstance()
    val format: DateFormat = SimpleDateFormat(APOD_DATE_FORMAT, Locale.getDefault())
    calendar.time = format.parse(date)
    return calendar
}

fun getApodFormattedDate(
    year: Int,
    month: Int,
    day: Int
): String {
    return year.toString() + "-" + (month + 1) + "-" + day
}