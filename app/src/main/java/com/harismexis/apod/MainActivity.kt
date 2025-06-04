package com.harismexis.apod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.harismexis.apod.route.ApodRoute
import com.harismexis.apod.dialog.DatePickerModal
import com.harismexis.apod.model.isImage
import com.harismexis.apod.model.isVideo
import com.harismexis.apod.ui.theme.NasaApisAppTheme
import com.harismexis.apod.viewmodel.ApodViewModel
import com.harismexis.apod.viewmodel.createViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NasaApisAppTheme {
                val viewModel: ApodViewModel = createViewModel()
                Scaffold(
                    topBar = { SmallTopAppBar(viewModel) },
                ) { padding ->
                    Column(modifier = Modifier.padding(padding)) {
                        ApodRoute(viewModel)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBar(viewModel: ApodViewModel) {
    var showDatePicker by remember { mutableStateOf(false) }
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            actionIconContentColor = Color.LightGray,
            containerColor = colorResource(R.color.black_1),
            titleContentColor = Color.LightGray,
        ),
        actions = {
            IconButton(onClick = {
                showDatePicker = true
            }) {
                Icon(
                    imageVector = Icons.Filled.DateRange,
                    contentDescription = "Localized description"
                )
            }
        },
        title = {
            Text("Picture of the day")
        }
    )
    if (showDatePicker) {
        DatePickerModal(
            { date ->
                viewModel.updateApod(date)
            }, {
                showDatePicker = false
            }
        )
    }
}
