package com.harismexis.apod.ui.view.home

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.SubcomposeAsyncImage
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import coil.imageLoader
import coil.request.ImageRequest
import com.harismexis.apod.ui.model.Apod
import com.harismexis.apod.ui.model.Media
import com.harismexis.apod.ui.model.isGif
import com.harismexis.apod.ui.view.videoplayer.ExoPlayer
import com.harismexis.apod.ui.view.videoplayer.ExoPlayerState
import com.harismexis.apod.ui.view.videoplayer.Youtube
import kotlinx.coroutines.flow.collectLatest

const val HOME_SCREEN = "HomeScreen"

@Composable
fun HomeScreen(
    apodVm: HomeViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val state: HomeViewModel.UiState by apodVm.state.collectAsStateWithLifecycle()
    val loading: Boolean by apodVm.loading.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        apodVm.error.collectLatest { message ->
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short,
            )
        }
    }

    when (state) {
        is HomeViewModel.UiState.InitialLoading -> EmptyScreen()
        is HomeViewModel.UiState.Success -> Apod(
            (state as HomeViewModel.UiState.Success).apod,
            apodVm
        )
    }

    if (loading) {
        LoadingIndicator()
    }
}

@Composable
private fun Apod(apod: Apod, viewModel: HomeViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top,
    ) {
        when (val media = apod.media) {
            is Media.Image -> {
                ImageView(media)
            }

            is Media.Video -> {
                ExoPlayer(
                    url = media.url,
                    playerState = viewModel.getExoPlayerState(media.url),
                    onPlayerReleased = { playerState ->
                        viewModel.updateExoPlayerState(playerState)
                    }
                )
            }

            is Media.YouTube -> {
                Youtube(
                    videoId = media.id,
                )
            }

            else -> {
                MediaPlaceholder(text = "Media type not supported")
            }
        }

        TextView(text = apod.date)
        Spacer(modifier = Modifier.height(16.dp))
        TextView(
            text = apod.title ?: "Title N/A",
            fontSize = 18.sp,
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextView(
            text = apod.explanation ?: "Explanation N/A",
            textAlign = TextAlign.Start,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun ImageView(media: Media.Image) {
    val context = LocalContext.current
    val gifLoader = remember {
        ImageLoader.Builder(context).components {
            if (Build.VERSION.SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()
    }

    val size = LocalWindowInfo.current.containerSize

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(
                if (size.height > size.width) 1f
                else 2.2f
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = ImageRequest.Builder(context)
                .data(media.url)
                .crossfade(200)
                .build(),
            imageLoader = if (media.isGif()) {
                gifLoader
            } else {
                context.imageLoader
            },
            contentScale = ContentScale.Crop,
            contentDescription = "NASA Picture of the Day",
            loading = { MediaPlaceholder(isLoading = true) },
            error = { MediaPlaceholder(text = "Failed to load image") },
        )
    }
}

@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {}
}

@Composable
private fun MediaPlaceholder(isLoading: Boolean = false, text: String? = null) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            TextView(
                text = text ?: "",
                fontSize = 18.sp,
            )
        }
    }
}

@Composable
private fun TextView(
    modifier: Modifier = Modifier.padding(start = 16.dp, end = 16.dp),
    text: String,
    fontSize: TextUnit = 16.sp,
    textAlign: TextAlign = TextAlign.Left,
) {
    Text(
        modifier = modifier,
        text = text.format(),
        lineHeight = 26.sp,
        style = typography.bodyLarge,
        textAlign = textAlign,
        color = MaterialTheme.colorScheme.onSurface,
        fontSize = fontSize,
    )
}

fun String.format(): String {
    return replace(
        Regex("[ \\t]{2,}"),
        " "
    )
}