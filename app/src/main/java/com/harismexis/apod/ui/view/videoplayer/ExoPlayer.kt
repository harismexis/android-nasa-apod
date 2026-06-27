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

data class ExoPlayerState(
    val videoPosition: Long = 0L,
    val playWhenReady: Boolean = true,
) {
    companion object {
        val Saver = androidx.compose.runtime.saveable.Saver<ExoPlayerState, List<Any?>>(
            save = {
                listOf(it.videoPosition, it.playWhenReady)
            },
            restore = {
                ExoPlayerState(
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

        var state by rememberSaveable(url, stateSaver = ExoPlayerState.Saver) {
            mutableStateOf(ExoPlayerState())
        }

        val player = remember(url) {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(url)
                setMediaItem(mediaItem)
                prepare()
            }
        }

        LaunchedEffect(player) {
            player.seekTo(state.videoPosition)
            player.playWhenReady = state.playWhenReady
        }

        LaunchedEffect(player) {
            while (true) {
                if (player.isPlaying) {
                    state = state.copy(videoPosition = player.currentPosition)
                }
                delay(1000.milliseconds)
            }
        }

        DisposableEffect(player) {
            val listener = object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    isLoading = playbackState == Player.STATE_BUFFERING
                            || playbackState == Player.STATE_IDLE
                }

                override fun onPositionDiscontinuity(
                    oldPosition: Player.PositionInfo,
                    newPosition: Player.PositionInfo,
                    reason: Int
                ) {
                    if (reason == Player.DISCONTINUITY_REASON_SEEK) {
                        state = state.copy(videoPosition = player.currentPosition)
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    state = state.copy(
                        playWhenReady = player.playWhenReady,
                        videoPosition = player.currentPosition
                    )
                }
            }

            player.addListener(listener)

            onDispose {
                player.removeListener(listener)
                player.release()
            }
        }

        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {
                PlayerView(it)
            },
            update = {
                it.player = player
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