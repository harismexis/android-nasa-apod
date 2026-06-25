package com.harismexis.apod.ui.view.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harismexis.apod.data.repository.LocalRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class GalleryViewModel(repository: LocalRepository) : ViewModel() {

    val apods = repository.getAllApodsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}