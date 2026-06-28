package com.harismexis.apod.ui.view.gallery

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.VideoFrameDecoder
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import com.harismexis.apod.ui.model.Apod
import com.harismexis.apod.ui.model.Media

const val GALLERY_SCREEN = "GalleryScreen"

@Composable
fun GalleryScreen(
    galleryVm: GalleryViewModel,
    onItemClick: (String) -> Unit,
) {
    val apods by galleryVm.apods.collectAsStateWithLifecycle()
    val size = LocalWindowInfo.current.containerSize

    LazyVerticalGrid(
        columns = GridCells.Fixed(
            count =
                if (size.height > size.width) 3
                else 6
        ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(4.dp),
    ) {
        items(
            items = apods,
            key = { it.date }
        ) { apod ->

            GalleryItem(
                apod = apod,
                onClick = {
                    onItemClick(apod.date)
                }
            )
        }
    }
}

@Composable
fun GalleryItem(
    apod: Apod,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (apod.media) {
                is Media.Image -> ImageThumbnail(apod = apod)
                is Media.Video -> VideoThumbnail(apod = apod)
                is Media.YouTube -> YoutubeThumbnail(apod = apod)
                else -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            Text(
                text = apod.date,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Composable
fun ImageThumbnail(apod: Apod) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),

        ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(apod.media.url)
                .crossfade(true)
                .build(),
            contentDescription = apod.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun VideoThumbnail(apod: Apod) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val context = LocalContext.current
        val imageLoader = remember {
            ImageLoader.Builder(context)
                .components {
                    add(VideoFrameDecoder.Factory())
                }
                .build()
        }

        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(apod.media.url)
                .videoFrameMillis(1000)
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = apod.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Icon(
            imageVector = Icons.Default.PlayCircleFilled,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp)
        )
    }
}

@Composable
fun YoutubeThumbnail(apod: Apod) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = "https://img.youtube.com/vi/${(apod.media as Media.YouTube).id}/hqdefault.jpg",
            contentDescription = apod.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Icon(
            imageVector = Icons.Default.PlayCircleFilled,
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(40.dp)
        )
    }
}