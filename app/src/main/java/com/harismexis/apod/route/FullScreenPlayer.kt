package com.harismexis.apod.route

import android.content.pm.ActivityInfo
import android.view.View
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.harismexis.apod.databinding.YoutubePlayerViewBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

const val FULL_SCREEN_PLAYER_SCREEN = "FullScreenPlayerScreen"
const val ARG_VIDEO_ID = "VIDEO_ID"

@Composable
fun FullScreenPlayerScreen(navController: NavHostController) {
    val videoId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(ARG_VIDEO_ID)
    videoId?.let {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            YoutubeViewBinding(videoId = it)
        }
    }
}

@Composable
private fun YoutubeViewBinding(videoId: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidViewBinding(YoutubePlayerViewBinding::inflate) {
        lifecycleOwner.lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addFullscreenListener(object : FullscreenListener {

            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                fullScreenViewContainer.addView(fullscreenView)
                val activity = youtubePlayerView.context as ComponentActivity
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            }

            override fun onExitFullscreen() {

            }
        })

        val playerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                player.loadVideo(videoId, 0f)
                player.toggleFullscreen()
            }
        }

        val options = IFramePlayerOptions.Builder()
            .controls(1)
            .build()

        youtubePlayerView.initialize(playerListener, options)
    }
}