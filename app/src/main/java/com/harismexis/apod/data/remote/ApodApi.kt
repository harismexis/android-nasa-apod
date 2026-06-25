package com.harismexis.apod.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * - NASA Astronomy Picture of the Day (APOD) API https://api.nasa.gov/
 * - Returns the astronomy picture of the day for a given date.
 * - Exposes a get request accepting the date and the api key as parameters.
 * - Date must be in the format "YYYY-MM-DD" e.g. 2026-05-17
 * - Passing null date will return the APOD for the current day.
 */
interface ApodApi {

    @GET("planetary/apod")
    suspend fun getApod(
        @Query("api_key") apiKey: String,
        @Query("date") date: String?
    ): ApodResponse
}

/**
 * Response from NASA Astronomy Picture of the Day (APOD) API.
 * The returned media can be an image or a video.
 * The following media types are supported:
 * - jpg e.g. https://apod.nasa.gov/apod/image/2603/cg4_1024.jpg
 * - png e.g. https://apod.nasa.gov/apod/image/2602/N147N1851024.png
 * - mp4 e.g. https://apod.nasa.gov/apod/image/2603/DepartingEarth_Messenger.mp4
 * - YouTube videos e.g.
 * - https://www.youtube.com/embed/hQFEHH5E69s?rel=0
 * - https://www.youtube.com/embed/hQFEHH5E69s?si=46r2TF9THNvtq1ep
 * - https://youtube.com/embed/hQFEHH5E69s?rel=0
 * - https://youtube.com/embed/hQFEHH5E69s?si=46r2TF9THNvtq1ep
 * - hQFEHH5E69s is the YouTube video ID.
 */
@Serializable
data class ApodResponse(
    val date: String, // "2009-04-20"
    val url: String? = null, // "https://apod.nasa.gov/apod/image/0904/sandmars_mro.jpg"
    val hdurl: String? = null, // "https://apod.nasa.gov/apod/image/0904/sandmars_mro_big.jpg"
    @SerialName("media_type")
    val mediaType: String? = null, // "image", "video" or "other" (1 occurrence)
    val title: String? = null, // "Flowing Barchan Sand Dunes on Mars"
    val explanation: String? = null, // "When does Mars act like a liquid...etc"
    @SerialName("service_version")
    val serviceVersion: String? = null, // "v1"
)

/**
 * Enum representing the values of the media_type field
 * of APOD response. It can be "image" or "video".
 * There is 1 case where media_type is "other" but
 * there is no media url so we ignore it.
 */
enum class ApodMediaType(val value: String) {
    IMAGE("image"),
    VIDEO("video"),
}

/**
 * Extension / util methods on [ApodResponse].
 */

fun ApodResponse.videoUrl(): String? {
    val videoUrl = when {
        url.isVideo() -> url
        hdurl.isVideo() -> hdurl
        mediaType == ApodMediaType.VIDEO.value -> {
            when {
                url.isNotNullAndNotYouTube() -> url
                hdurl.isNotNullAndNotYouTube() -> hdurl
                else -> null
            }
        }

        else -> null
    }
    return videoUrl?.normalizeUrl()
}

fun ApodResponse.imageUrl(): String? {
    val imageUrl = when {
        url.isImage() -> url
        hdurl.isImage() -> hdurl
        mediaType == ApodMediaType.IMAGE.value -> url ?: hdurl
        else -> null
    }
    return imageUrl?.normalizeUrl()
}

fun String?.isVideo(): Boolean {
    return this?.endsWith(".mp4", ignoreCase = true) == true
}

fun String?.isNotNullAndNotYouTube(): Boolean {
    return this != null && extractYoutubeId() == null
}

fun String?.isImage(): Boolean {
    return this?.endsWith(".jpg", ignoreCase = true) == true
            || this?.endsWith(".png", ignoreCase = true) == true
            || this?.endsWith(".jpeg", ignoreCase = true) == true
            || this?.endsWith(".gif", ignoreCase = true) == true
}

fun ApodResponse.youtubeId(): String? {
    url.extractYoutubeId()?.let {
        return it
    }
    hdurl.extractYoutubeId()?.let {
        return it
    }
    return null
}

fun String?.extractYoutubeId(): String? {
    if (this == null) return null
    val regex = Regex("""(?:https?://)?(?:www\.)?youtube\.com/embed/([^?&/]+)""")
    return regex.find(this)?.groupValues?.getOrNull(1)
}

fun String.normalizeUrl(): String {
    return if (startsWith("http:")) {
        replaceFirst("http:", "https:")
    } else {
        this
    }
}
