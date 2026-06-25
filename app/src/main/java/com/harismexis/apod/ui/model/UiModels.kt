package com.harismexis.apod.ui.model

/**
 * Model classes that will be used in the UI.
 */

data class Apod(
    val date: String,
    val explanation: String?,
    val media: Media,
    val title: String?,
)

sealed class Media {

    abstract val url: String?

    data class Image(override val url: String) : Media()
    data class Video(override val url: String) : Media()
    data class YouTube(val id: String) : Media() {
        override val url: String
            get() = "https://www.youtube.com/watch?v=${id}"
    }

    data class Unknown(override val url: String?) : Media()
}

fun Media.isGif(): Boolean {
    return url?.endsWith(".gif", ignoreCase = true) == true
}
