package com.harismexis.apod.ui.view.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.LastPage
import androidx.compose.material.icons.filled.BrowseGallery
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.FirstPage
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harismexis.apod.util.isTablet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    enabled: Boolean,
    getInitialDate: () -> Long?,
    onFirstDate: () -> Unit,
    onPreviousDate: () -> Unit,
    onDateSelected: (Long?) -> Unit,
    onNextDate: () -> Unit,
    onLastDate: () -> Unit,
    onBrowserClick: () -> Unit,
    onGalleryClick: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
        ),
        navigationIcon = {
            Text(
                text = "APOD",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 12.dp),
                fontSize = 18.sp,
            )
        },
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                IconButton(
                    enabled = enabled,
                    onClick = {
                        onFirstDate()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.FirstPage,
                        contentDescription = "Loads Previous Date"
                    )
                }
                IconButton(
                    enabled = enabled,
                    onClick = {
                        onPreviousDate()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronLeft,
                        contentDescription = "Loads Previous Date"
                    )
                }
                IconButton(
                    enabled = enabled,
                    onClick = {
                        showDatePicker = true
                    }) {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "Opens Date Selector"
                    )
                }
                IconButton(
                    enabled = enabled,
                    onClick = {
                        onNextDate()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.ChevronRight,
                        contentDescription = "Loads Next Date"
                    )
                }
                IconButton(
                    enabled = enabled,
                    onClick = {
                        onLastDate()
                    }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.LastPage,
                        contentDescription = "Loads Today"
                    )
                }
            }
        },
        actions = {
            if (isTablet()) {
                IconButton(
                    enabled = enabled,
                    onClick = {
                        onGalleryClick()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.PhotoLibrary,
                        contentDescription = "Gallery"
                    )
                }
                IconButton(
                    enabled = enabled,
                    onClick = {
                        onBrowserClick()
                    }) {
                    Icon(
                        imageVector = Icons.Filled.Language,
                        contentDescription = "Loads Today"
                    )
                }
                IconButton(
                    enabled = enabled,
                    onClick = {
                        // TODO: Navigate to Settings
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            } else {
                IconButton(
                    enabled = enabled,
                    onClick = { showMenu = true }
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options"
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Gallery") },
                        onClick = {
                            showMenu = false
                            onGalleryClick()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Web Browser") },
                        onClick = {
                            showMenu = false
                            onBrowserClick()
                        }
                    )

                    DropdownMenuItem(
                        text = { Text("Settings") },
                        onClick = {
                            showMenu = false
                            // TODO: Navigate to Settings
                        }
                    )
                }
            }
        }
    )

    if (showDatePicker) {
        DatePicker(
            initialSelectedDateMillis = getInitialDate(),
            onDateSelected = { date ->
                onDateSelected(date)
            },
            onDismiss = {
                showDatePicker = false
            }
        )
    }
}