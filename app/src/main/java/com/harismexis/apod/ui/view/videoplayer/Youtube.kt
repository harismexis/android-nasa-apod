package com.harismexis.apod.ui.view.videoplayer

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@Composable
fun Youtube(
    modifier: Modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
    videoId: String,
    videoPosition: Float = 0f,
    playWhenReady: Boolean = true,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val playerView = remember {
        YouTubePlayerView(context).apply {
            enableAutomaticInitialization = false
        }
    }

    var player by remember {
        mutableStateOf<YouTubePlayer?>(null)
    }

    DisposableEffect(playerView) {
        val listener = object : AbstractYouTubePlayerListener() {

            override fun onReady(youTubePlayer: YouTubePlayer) {
                player = youTubePlayer
            }
        }

        val options = IFramePlayerOptions.Builder(context)
            .controls(1)
            .build()

        playerView.initialize(listener, options)

        onDispose {
            playerView.release()
        }
    }

    DisposableEffect(lifecycleOwner, playerView) {
        lifecycleOwner.lifecycle.addObserver(playerView)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(playerView)
        }
    }

    LaunchedEffect(videoId, player) {
        player?.let { youtubePlayer ->
            if (playWhenReady) {
                youtubePlayer.loadVideo(videoId, videoPosition)
            } else {
                youtubePlayer.cueVideo(videoId, videoPosition)
            }
        }
    }

    LaunchedEffect(playWhenReady, player) {
        player?.let {
            if (playWhenReady) {
                it.play()
            } else {
                it.pause()
            }
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { playerView }
    )
}