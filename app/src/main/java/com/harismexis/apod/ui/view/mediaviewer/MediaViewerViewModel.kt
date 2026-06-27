package com.harismexis.apod.ui.view.mediaviewer

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harismexis.apod.data.repository.LocalRepository
import com.harismexis.apod.ui.model.Apod
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MediaViewerViewModel(private val repository: LocalRepository) : ViewModel() {

    companion object {
        const val TAG = "MediaViewerViewModel"
    }

    private val _apod = MutableStateFlow<Apod?>(null)
    val apod = _apod.asStateFlow()

    fun fetchApod(date: String) {
        viewModelScope.launch {
            val apod = repository.getApod(date)
            Log.d(TAG, "Fetched apod from room: $apod")
            _apod.value = apod
        }
    }
}