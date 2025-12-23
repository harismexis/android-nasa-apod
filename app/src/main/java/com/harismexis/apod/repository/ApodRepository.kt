package com.harismexis.apod.repository

import android.util.Log
import com.google.gson.Gson
import com.harismexis.apod.BuildConfig
import com.harismexis.apod.model.Apod
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume

class ApodRepository {

    private val tag = "ApodRepository"

    private var gson: Gson = Gson()
    private val client = OkHttpClient()

    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun getApod(date: String? = null): Apod? {
        return suspendCancellableCoroutine { continuation ->
            val apiKey = BuildConfig.NASA_API_KEY
            val httpUrl: HttpUrl = HttpUrl.Builder()
                .scheme("https")
                .host("api.nasa.gov")
                .addPathSegment("planetary")
                .addPathSegment("apod")
                .addQueryParameter("date", date)
                .addQueryParameter("api_key", apiKey)
                .build()

            val request = Request.Builder()
                .url(httpUrl)
                .build()

            val call = client.newCall(request)
            continuation.invokeOnCancellation {
                call.cancel()
            }
            call.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resume(value = null)
                }

                override fun onResponse(call: Call, response: Response) {
                    val result = response.body.string()
                    val apod = gson.fromJson(result, Apod::class.java)
                    val log = apod.copy(explanation = "skipped")
                    Log.d(tag, "apod::$log")
                    continuation.resume(value = apod)
                }
            })
        }
    }
}