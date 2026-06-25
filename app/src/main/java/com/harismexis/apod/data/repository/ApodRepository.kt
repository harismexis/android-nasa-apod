package com.harismexis.apod.data.repository

import android.util.Log
import com.harismexis.apod.BuildConfig
import com.harismexis.apod.data.remote.ApodApi
import com.harismexis.apod.data.remote.ApodResponse
import com.harismexis.apod.data.remote.imageUrl
import com.harismexis.apod.data.remote.normalizeUrl
import com.harismexis.apod.data.remote.videoUrl
import com.harismexis.apod.data.remote.youtubeId
import com.harismexis.apod.data.database.ApodDao
import com.harismexis.apod.ui.model.Apod
import com.harismexis.apod.ui.model.Media
import com.harismexis.apod.util.toUiDate
import kotlinx.coroutines.CancellationException

/**
 * Returns the astronomy Picture of the Day (APOD) for a given date.
 * If the APOD for the given date is already stored in the local database, it returns the cached version.
 * Otherwise, it fetches the APOD from the remote API, stores it in the database and returns it.
 */
class ApodRepository(
    val api: ApodApi,
    val dao: ApodDao,
) {
    companion object {
        const val TAG = "ApodRepository"
    }

    suspend fun getApod(date: String): Result<Apod> =
        try {
            dao.getApod(date)?.let {
                val uiModel = it.toUiModel()
                Log.d(TAG, "uiModel room: ${uiModel.copy(explanation = "")}")
                return Result.success(uiModel)
            }
            val response = api.getApod(BuildConfig.NASA_API_KEY, date)
            Log.d(TAG, "response: ${response.copy(explanation = "")}")
            val entity = response.toEntity()
            dao.insert(entity)
            val uiModel = response.toUiModel()
            Log.d(TAG, "uiModel remote: ${uiModel.copy(explanation = "")}")
            return Result.success(uiModel)
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching APOD: ${e.message ?: "Unknown error"}")
            if (e is CancellationException) {
                throw e
            }
            return Result.failure(e)
        }
}

fun ApodResponse.toUiModel(): Apod {
    return Apod(
        date = date.toUiDate(),
        explanation = explanation,
        media = media(),
        title = title,
    )
}

fun ApodResponse.media(): Media {
    imageUrl()?.let {
        return Media.Image(it)
    }
    videoUrl()?.let {
        return Media.Video(it)
    }
    youtubeId()?.let {
        return Media.YouTube(it)
    }
    return Media.Unknown((url ?: hdurl)?.normalizeUrl())
}
