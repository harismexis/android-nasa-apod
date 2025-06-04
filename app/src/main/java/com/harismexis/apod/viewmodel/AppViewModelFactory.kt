package com.harismexis.apod.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

object AppViewModelFactory : ViewModelProvider.Factory by viewModelFactory({
    initializer {
        ApodViewModel()
    }
})

@Composable
inline fun <reified VM : ViewModel> createViewModel(): VM =
    viewModel(factory = AppViewModelFactory)
