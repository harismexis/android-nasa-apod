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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.harismexis.apod.screens.APOD_SCREEN
import com.harismexis.apod.screens.ARG_VIDEO_ID
import com.harismexis.apod.screens.ApodScreen
import com.harismexis.apod.screens.PLAYER_SCREEN
import com.harismexis.apod.screens.PlayerScreen
import com.harismexis.apod.screens.PREF_SCREEN
import com.harismexis.apod.screens.PrefScreen
import com.harismexis.apod.screens.SmallTopAppBar
import com.harismexis.apod.ui.theme.NasaApisAppTheme
import com.harismexis.apod.viewmodel.ApodVm

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NasaApisAppTheme {
                App()
            }
        }
    }

    @Composable
    private fun App(
        navController: NavHostController = rememberNavController(),
        apodVm: ApodVm = viewModel(),
    ) {
        val videoId = apodVm.videoId.collectAsStateWithLifecycle().value

        LaunchedEffect(Unit) {
            apodVm.updateApod()
        }

        val backStackEntry: State<NavBackStackEntry?> = navController.currentBackStackEntryAsState()
        val isHomeScreen = backStackEntry.value?.destination?.route == APOD_SCREEN
        val isPlayerFullScreen = backStackEntry.value?.destination?.route == PLAYER_SCREEN

        Scaffold(
            topBar = {
                if (!isPlayerFullScreen) {
                    SmallTopAppBar(
                        onDateSelected = { date ->
                            apodVm.updateApod(date)
                        },
                        onSettingsClicked = {
                            navController.navigate(route = PREF_SCREEN)
                        },
                        onFullScreenPlayerClicked = {
                            navController.currentBackStackEntry?.savedStateHandle?.set(
                                ARG_VIDEO_ID,
                                videoId,
                            )
                            navController.navigate(PLAYER_SCREEN)
                        },
                        canNavigateBack = !isHomeScreen,
                        navigateUp = {
                            navController.navigateUp()
                        },
                    )
                }
            },
        ) { padding ->
            NavHostBuilder(navController, apodVm, padding)
        }
    }
}

@Composable
private fun NavHostBuilder(
    navController: NavHostController,
    apodVm: ApodVm,
    padding: PaddingValues,
) {
    NavHost(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState()),
        navController = navController,
        startDestination = APOD_SCREEN,
    ) {
        composable(route = APOD_SCREEN) {
            ApodScreen(apodVm)
        }
        composable(route = PLAYER_SCREEN) {
            PlayerScreen(navController)
        }
        composable(route = PREF_SCREEN) {
            PrefScreen()
        }
    }
}
