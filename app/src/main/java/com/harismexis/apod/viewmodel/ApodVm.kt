package com.harismexis.apod.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harismexis.apod.model.Apod
import com.harismexis.apod.repository.ApodRepository
import com.harismexis.apod.util.convertMillisToApodDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApodVm(private val repo: ApodRepository = ApodRepository()) : ViewModel() {

    private val _apod = MutableStateFlow<Apod?>(null)
    val apod: StateFlow<Apod?> = _apod.asStateFlow()

    private val _isLoading = MutableStateFlow<Boolean>(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _videoId = MutableStateFlow<String>("")
    val videoId: StateFlow<String> = _videoId.asStateFlow()

    fun updateApod(date: Long? = null) {
        _isLoading.value = true
        val dateString: String? = if (date == null) null else convertMillisToApodDate(date)
        viewModelScope.launch {
            _apod.value = repo.getApod(dateString)
            _isLoading.value = false
        }
    }
}