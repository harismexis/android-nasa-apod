package com.harismexis.apod.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.harismexis.apod.R
import com.harismexis.apod.dialog.DatePickerModal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(
    onDateSelected: (Long?) -> Unit,
    onSettingsClicked: () -> Unit,
    onFullScreenPlayerClicked: () -> Unit,
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit = {},
) {
    var showDatePicker by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = Color.LightGray,
            actionIconContentColor = Color.LightGray,
            containerColor = colorResource(R.color.black_1),
            titleContentColor = Color.LightGray,
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = { navigateUp() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Navigates back"
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = {
                onFullScreenPlayerClicked()
            }) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Opens Preference Screen"
                )
            }
            IconButton(onClick = {
                onSettingsClicked()
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Opens Preference Screen"
                )
            }
            IconButton(onClick = {
                showDatePicker = true
            }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Opens Date Selector"
                )
            }
        },
        title = {
            Text("Apod")
        },
    )
    if (showDatePicker) {
        DatePickerModal(
            { date ->
                onDateSelected(date)
            }, {
                showDatePicker = false
            }
        )
    }
}