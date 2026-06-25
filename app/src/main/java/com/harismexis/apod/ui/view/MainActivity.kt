package com.harismexis.apod.ui.view

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.harismexis.apod.ui.theme.NasaApodAppTheme
import com.harismexis.apod.ui.view.home.HOME_SCREEN
import com.harismexis.apod.ui.view.home.HomeViewModel
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.harismexis.apod.app.getViewModel
import com.harismexis.apod.ui.view.gallery.GALLERY_SCREEN
import com.harismexis.apod.ui.view.gallery.GalleryScreen
import com.harismexis.apod.ui.view.gallery.GalleryTopBar
import com.harismexis.apod.ui.view.home.HomeScreen
import com.harismexis.apod.ui.view.home.HomeTopBar
import com.harismexis.apod.ui.view.mediaviewer.MEDIA_VIEWER_SCREEN
import com.harismexis.apod.ui.view.mediaviewer.MediaViewerScreen
import com.harismexis.apod.util.toMillis

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            NasaApodAppTheme(
                darkTheme = true,
                dynamicColor = true,
                content = { App() }
            )
        }
    }

    @Composable
    private fun App(
        navController: NavHostController = rememberNavController(),
        apodVm: HomeViewModel = getViewModel(),
    ) {
        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val isLoading by apodVm.loading.collectAsStateWithLifecycle()

        fun showSnackbar(
            message: String,
            duration: SnackbarDuration = SnackbarDuration.Short,
        ) {
            scope.launch {
                snackbarHostState.showSnackbar(message = message, duration = duration)
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                when (currentRoute) {
                    HOME_SCREEN -> HomeTopBar(
                        enabled = !isLoading,
                        getInitialDate = {
                            apodVm.selectedDate.toMillis()
                        },
                        onFirstDate = {
                            apodVm.loadFirstApod()
                        },
                        onPreviousDate = {
                            if (apodVm.isFirstApodDay()) {
                                showSnackbar("Cannot go back. Already viewing the first APOD")
                            } else {
                                apodVm.loadPreviousApod()
                            }
                        },
                        onDateSelected = { date ->
                            apodVm.loadApod(date)
                        },
                        onNextDate = {
                            if (apodVm.isToday()) {
                                showSnackbar("Cannot go forward. Already viewing today's APOD")
                            } else {
                                apodVm.loadNextApod()
                            }
                        },
                        onLastDate = {
                            apodVm.loadApod()
                        },
                        onBrowserClick = {
                            apodVm.mediaUrl()?.let {
                                context.startActivity(Intent(Intent.ACTION_VIEW, it.toUri()))
                            }
                        },
                        onGalleryClick = {
                            navController.navigate(GALLERY_SCREEN)
                        }
                    )
                    GALLERY_SCREEN -> GalleryTopBar(
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                navController = navController,
                startDestination = HOME_SCREEN,
            ) {
                composable(route = HOME_SCREEN) {
                    HomeScreen(apodVm, snackbarHostState)
                }
                composable(GALLERY_SCREEN) {
                    GalleryScreen(
                        galleryVm = getViewModel(),
                        onItemClick = { date ->
                            navController.navigate(
                                "$MEDIA_VIEWER_SCREEN/${Uri.encode(date)}"
                            )
                        }
                    )
                }
                composable(
                    route = "$MEDIA_VIEWER_SCREEN/{date}",
                    arguments = listOf(
                        navArgument("date") {
                            type = NavType.StringType
                        }
                    )
                ) { backStackEntry ->
                    MediaViewerScreen(
                        date = backStackEntry.arguments?.getString("date").orEmpty(),
                        viewModel = getViewModel(),
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }
}
