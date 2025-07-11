package com.harismexis.apod.screens

import android.content.pm.ActivityInfo
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.harismexis.apod.databinding.YoutubePlayerViewBinding
import com.harismexis.apod.screens.components.HideSystemBars
import com.harismexis.apod.screens.components.LockScreenOrientation
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.FullscreenListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

const val PLAYER_SCREEN = "PlayerScreen"
const val ARG_VIDEO_ID = "VIDEO_ID"
const val DEFAULT_VIDEO_ID = "rQcKIN9vj3U"

@Composable
fun PlayerScreen(navController: NavHostController) {
    val videoId = navController.previousBackStackEntry?.savedStateHandle?.get<String>(ARG_VIDEO_ID)
    videoId?.let {
        YoutubeView(videoId = it)
    }
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
    HideSystemBars()
}

@Composable
private fun YoutubeView(videoId: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidViewBinding(
        factory = YoutubePlayerViewBinding::inflate,
        modifier = Modifier.fillMaxSize(),
    ) {
        lifecycleOwner.lifecycle.addObserver(youtubePlayerView)
        youtubePlayerView.addFullscreenListener(object : FullscreenListener {
            override fun onEnterFullscreen(fullscreenView: View, exitFullscreen: () -> Unit) {
                youtubePlayerView.visibility = View.GONE
                fullScreenViewContainer.visibility = View.VISIBLE
                fullScreenViewContainer.addView(fullscreenView)
            }

            override fun onExitFullscreen() {
                youtubePlayerView.visibility = View.VISIBLE
                fullScreenViewContainer.visibility = View.GONE
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