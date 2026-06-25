package com.harismexis.apod.ui.view.home

import androidx.compose.foundation.layout.Row
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import com.harismexis.apod.util.firstApodDate
import com.harismexis.apod.util.toMillis
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    initialSelectedDateMillis: Long? = null,
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val firstApodDateMillis = firstApodDate.toMillis()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialSelectedDateMillis,
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return utcTimeMillis in firstApodDateMillis..LocalDate.now().toMillis()
            }
        }
    )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Row {
                TextButton(
                    onClick = {
                        val todayMillis = LocalDate.now().toMillis()
                        datePickerState.selectedDateMillis = todayMillis
                        datePickerState.displayedMonthMillis = todayMillis
                    }
                ) {
                    Text("Today")
                }
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}