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
import androidx.media3.common.Timeline
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

data class ExoPlayerState(
    val id: String? = null,
    val videoPosition: Long = 0L,
    val playWhenReady: Boolean = true,
)

@Composable
fun ExoPlayer(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .padding(16.dp),
    url: String, // https://apod.nasa.gov/apod/image/2603/DepartingEarth_Messenger.mp4
    playerState: ExoPlayerState? = null,
    onPlayerReleased: (state: ExoPlayerState) -> Unit = {},
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        var isLoading by remember { mutableStateOf(true) }
        val context = LocalContext.current

        fun retrieveState(): ExoPlayerState? {
            return if (playerState != null && url == playerState.id) {
                playerState
            } else {
                null
            }
        }

        val exoPlayer = remember(url) {
            val state = retrieveState()
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                if (state != null) {
                    seekTo(state.videoPosition)
                    this.playWhenReady = state.playWhenReady
                } else {
                    this.playWhenReady = true
                }
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
                onPlayerReleased.invoke(
                    ExoPlayerState(
                        id = url,
                        videoPosition = exoPlayer.currentPosition,
                        playWhenReady = exoPlayer.playWhenReady,
                    )
                )
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