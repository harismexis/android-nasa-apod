package com.harismexis.apod.ui.view.mediaviewer

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.harismexis.apod.ui.model.Media
import com.harismexis.apod.ui.view.videoplayer.ExoPlayer
import com.harismexis.apod.ui.view.videoplayer.Youtube
import com.harismexis.apod.util.toApodDate

const val MEDIA_VIEWER_SCREEN = "MediaViewerScreen"

@Composable
fun MediaViewerScreen(
    date: String,
    viewModel: MediaViewerViewModel,
    onBack: () -> Unit,
) {
    BackHandler {
        onBack()
    }

    val apod by viewModel.apod.collectAsStateWithLifecycle()
    LaunchedEffect(date) {
        viewModel.fetchApod(date.toApodDate())
    }

    EnableImmersiveMode()

    val size = LocalWindowInfo.current.containerSize

    when (val media = apod?.media) {
        null -> {
            Box(modifier = Modifier.fillMaxSize())
        }

        is Media.Unknown -> {
            Box(modifier = Modifier.fillMaxSize())
        }

        is Media.Image -> {
            ZoomableImage(url = media.url)
        }

        is Media.Video -> {
            ExoPlayer(
                modifier = if (size.height > size.width) {
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                } else {
                    Modifier.fillMaxSize()
                },
                media.url,
            )
        }

        is Media.YouTube -> {
            Youtube(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp),
                videoId = media.id,
            )
        }
    }
}

@Composable
fun ZoomableImage(
    modifier: Modifier = Modifier,
    url: String,
) {
    var scale by remember {
        mutableFloatStateOf(1f)
    }

    var offset by remember {
        mutableStateOf(Offset.Zero)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->
                    scale = (scale * zoom).coerceIn(1f, 5f)
                    offset += pan
                }
            }
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale

                    translationX = offset.x
                    translationY = offset.y
                }
        )
    }
}

@Composable
fun EnableImmersiveMode() {
    val activity = requireNotNull(LocalActivity.current)
    val view = activity.window.decorView

    SideEffect {
        WindowCompat.setDecorFitsSystemWindows(activity.window, false)
        WindowCompat.getInsetsController(activity.window, view).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            WindowCompat.getInsetsController(activity.window, view)
                .show(WindowInsetsCompat.Type.systemBars())

            WindowCompat.setDecorFitsSystemWindows(activity.window, true)
        }
    }
}

