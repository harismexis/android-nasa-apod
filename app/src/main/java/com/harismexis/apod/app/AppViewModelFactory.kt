package com.harismexis.apod.app

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.InitializerViewModelFactoryBuilder
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.harismexis.apod.ui.view.home.HomeViewModel
import com.harismexis.apod.ui.view.gallery.GalleryViewModel
import com.harismexis.apod.ui.view.mediaviewer.MediaViewerViewModel

@Composable
inline fun <reified VM : ViewModel> getViewModel(): VM = viewModel(factory = AppViewModelFactory)

object AppViewModelFactory : ViewModelProvider.Factory by viewModelFactory({
    createViewModel {
        HomeViewModel(apodRepository)
    }
    createViewModel {
        GalleryViewModel(localRepository)
    }
    createViewModel {
        MediaViewerViewModel(localRepository)
    }
})

private inline fun <reified VM : ViewModel> InitializerViewModelFactoryBuilder.createViewModel(
    crossinline initialize: DependencyContainer.() -> VM
) {
    initializer {
        val application = get(APPLICATION_KEY)
        val dependencies = checkNotNull(application).dependencies
        dependencies.initialize()
    }
}


