package com.harismexis.apod.ui.view.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harismexis.apod.data.repository.ApodRepository
import com.harismexis.apod.ui.model.Apod
import com.harismexis.apod.util.firstApodDate
import com.harismexis.apod.util.toApodDate
import com.harismexis.apod.util.toLocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(private val repository: ApodRepository) : ViewModel() {

    companion object {
        const val TAG = "HomeViewModel"
    }

    sealed interface UiState {
        data object InitialLoading : UiState
        data class Success(val apod: Apod) : UiState
    }

    private val _state = MutableStateFlow<UiState>(UiState.InitialLoading)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    private val _error = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val error: SharedFlow<String> = _error.asSharedFlow()

    var selectedDate: LocalDate = LocalDate.now()
        private set

    init {
        fetchApod(selectedDate)
    }

    fun isToday(): Boolean = selectedDate == LocalDate.now()

    fun isFirstApodDay(): Boolean = selectedDate == firstApodDate

    fun loadApod(date: Long? = null) {
        val date = date?.toLocalDate() ?: LocalDate.now()
        fetchApod(date)
    }

    fun loadNextApod() {
        val date = selectedDate.plusDays(1)
        fetchApod(date)
    }

    fun loadPreviousApod() {
        val date = selectedDate.minusDays(1)
        fetchApod(date)
    }

    fun loadFirstApod() {
        fetchApod(firstApodDate)
    }

    private fun fetchApod(date: LocalDate) {
        viewModelScope.launch {
            _loading.value = true
            val apiDate = date.toApodDate()
            repository.getApod(apiDate).onSuccess { uiModel ->
                _state.value = UiState.Success(uiModel)
                selectedDate = date
            }.onFailure { e ->
                val error = e.message ?: "Unknown error"
                Log.d(TAG, error)
                _error.tryEmit(error)
                if (e is CancellationException) {
                    throw e
                }
            }
            _loading.value = false
        }
    }

    fun mediaUrl(): String? = when (val uiState = state.value) {
        is UiState.Success -> {
            uiState.apod.media.url
        }

        else -> {
            null
        }
    }
}