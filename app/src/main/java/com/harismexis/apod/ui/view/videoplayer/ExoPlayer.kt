package com.harismexis.apod.ui.view.videoplayer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

data class ExoState(
    val videoPosition: Long = 0L,
    val playWhenReady: Boolean = true,
) {
    companion object {
        val Saver = androidx.compose.runtime.saveable.Saver<ExoState, List<Any?>>(
            save = {
                listOf(
                    it.videoPosition,
                    it.playWhenReady
                )
            },
            restore = {
                ExoState(
                    videoPosition = it[0] as Long,
                    playWhenReady = it[1] as Boolean
                )
            }
        )
    }
}


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
        val context = LocalContext.current

        var isLoading by remember { mutableStateOf(true) }
        var state by rememberSaveable(url, stateSaver = ExoState.Saver) {
            mutableStateOf(ExoState())
        }

        val exoPlayer = remember(url) {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
                seekTo(state.videoPosition)
                this.playWhenReady = state.playWhenReady

            }
        }

        LaunchedEffect(exoPlayer) {
            while (true) {
                if (exoPlayer.isPlaying) {
                    state = state.copy(videoPosition = exoPlayer.currentPosition)
                }
                delay(1000.milliseconds)
            }
        }

        DisposableEffect(exoPlayer) {
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    isLoading = state == Player.STATE_BUFFERING || state == Player.STATE_IDLE
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                        state = state.copy(videoPosition = exoPlayer.currentPosition)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    state = state.copy(
                        playWhenReady = exoPlayer.playWhenReady,
                        videoPosition = exoPlayer.currentPosition
                    )
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