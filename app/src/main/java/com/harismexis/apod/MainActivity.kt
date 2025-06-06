package com.harismexis.apod

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harismexis.apod.route.APOD_SCREEN
import com.harismexis.apod.route.ApodScreen
import com.harismexis.apod.route.PREF_SCREEN
import com.harismexis.apod.route.PrefScreen
import com.harismexis.apod.route.SmallTopAppBar
import com.harismexis.apod.ui.theme.NasaApisAppTheme
import com.harismexis.apod.viewmodel.ApodViewModel

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NasaApisAppTheme {
                val navController = rememberNavController()
                val apodViewModel: ApodViewModel = viewModel()
                Scaffold(
                    topBar = {
                        SmallTopAppBar(
                            onDateSelected = { date ->
                                apodViewModel.updateApod(date)
                            },
                            onSettingsClicked = {
                                navController.navigate(PREF_SCREEN)
                            },
                        )
                    },
                ) { padding ->
                    NavHostBuilder(navController, apodViewModel, padding)
                }
            }
        }
    }
}

@Composable
private fun NavHostBuilder(
    navController: NavHostController,
    apodViewModel: ApodViewModel,
    padding: PaddingValues,
) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        navController = navController,
        startDestination = APOD_SCREEN
    ) {
        composable(APOD_SCREEN) {
            ApodScreen(apodViewModel)
        }
        composable(PREF_SCREEN) {
            PrefScreen()
        }
    }
}
