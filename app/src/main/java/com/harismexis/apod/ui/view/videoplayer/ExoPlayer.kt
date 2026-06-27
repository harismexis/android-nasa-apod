package com.harismexis.apod.ui.view.videoplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun ExoPlayer(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .padding(16.dp),
    url: String, // https://apod.nasa.gov/apod/image/2603/DepartingEarth_Messenger.mp4
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        var isLoading by remember { mutableStateOf(true) }
        val context = LocalContext.current

        val exoPlayer = remember(url) {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }

        DisposableEffect(exoPlayer) {
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    isLoading = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                }
            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it)
            },
            update = {
                it.player = exoPlayer
                it.useController = true
            }
        )

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = Alignment.Center)
            )
        }
    }
}