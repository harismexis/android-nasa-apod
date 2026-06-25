package com.harismexis.apod.data.repository

import com.harismexis.apod.data.database.ApodDao
import com.harismexis.apod.data.database.ApodEntity
import com.harismexis.apod.data.remote.ApodResponse
import com.harismexis.apod.ui.model.Apod
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Returns the astronomy Picture of the Day (APOD) for a given date.
 */
class LocalRepository(
    val dao: ApodDao,
) {

    suspend fun getApod(date: String): Apod? {
        return dao.getApod(date)?.toUiModel()
    }

    fun getAllApodsFlow(): Flow<List<Apod>> {
        return dao.getAllApodsFlow()
            .map { entities ->
                entities.map { it.toUiModel() }
            }
    }
}

fun ApodEntity.toResponse() =
    ApodResponse(
        date = date,
        title = title,
        explanation = explanation,
        mediaType = mediaType,
        url = url,
        hdurl = hdurl
    )

fun ApodResponse.toEntity() =
    ApodEntity(
        date = date,
        title = title,
        explanation = explanation,
        mediaType = mediaType,
        url = url,
        hdurl = hdurl
    )

fun ApodEntity.toUiModel() = toResponse().toUiModel()

