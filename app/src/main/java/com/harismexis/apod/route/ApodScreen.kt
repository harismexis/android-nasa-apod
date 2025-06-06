package com.harismexis.apod.route

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.SubcomposeAsyncImage
import com.harismexis.apod.databinding.YoutubePlayerViewBinding
import com.harismexis.apod.model.Apod
import com.harismexis.apod.model.extractYouTubeVideoId
import com.harismexis.apod.model.isImage
import com.harismexis.apod.viewmodel.ApodViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions

// Astronomy Picture of the Day

const val APOD_SCREEN = "ApodScreen"

@Composable
fun ApodScreen(viewModel: ApodViewModel) {
    val isLoading: Boolean = viewModel.isLoading.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        viewModel.updateApod()
    }

    if (isLoading) {
        LoadingView()
    } else {
        ApodContent(viewModel)
    }
}

@Composable
private fun ApodContent(viewModel: ApodViewModel) {
    val apod: Apod? = viewModel.apod.collectAsStateWithLifecycle().value
    val isImage = apod?.isImage() == true

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.DarkGray)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isImage) {
                SubcomposeAsyncImage(
                    model = apod.url,
                    contentDescription = "Translated description of what the image contains",
                    loading = { CircularProgressIndicator() },
                    error = { CircularProgressIndicator() },
                )
            } else {
                apod?.url.extractYouTubeVideoId()?.let {
                    YoutubeViewBinding(videoId = it)
                }
            }
        }

        ApodInfo(apod)
    }
}

@Composable
private fun ApodInfo(apod: Apod?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 0.dp, 16.dp, 16.dp),
    ) {
        Text(
            text = apod?.title ?: "Title N/A",
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 4.dp),
            textAlign = TextAlign.Left,
            lineHeight = 18.sp,
            color = Color.White,
            style = typography.displayMedium,
            fontSize = 18.sp,
        )
        Text(
            text = apod?.date ?: "Date N/A",
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 4.dp),
            textAlign = TextAlign.Left,
            lineHeight = 16.sp,
            color = Color.White,
            style = typography.displayMedium,
            fontSize = 16.sp,
        )
        Text(
            text = apod?.explanation ?: "Explanation N/A",
            modifier = Modifier.padding(0.dp, 4.dp, 0.dp, 4.dp),
            textAlign = TextAlign.Left,
            lineHeight = 18.sp,
            color = Color.White,
            style = typography.displayMedium,
            fontSize = 16.sp,
        )
    }
}

@Composable
private fun YoutubeViewBinding(videoId: String) {
    val lifecycleOwner = LocalLifecycleOwner.current
    AndroidViewBinding(YoutubePlayerViewBinding::inflate) {
        lifecycleOwner.lifecycle.addObserver(youtubePlayerView)
        val playerListener = object : AbstractYouTubePlayerListener() {
            override fun onReady(player: YouTubePlayer) {
                player.loadVideo(videoId, 0f)
            }
        }

        val options = IFramePlayerOptions.Builder()
            .controls(1)
            .build()

        youtubePlayerView.initialize(playerListener, options)
    }
}

@Composable
private fun LoadingView() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}