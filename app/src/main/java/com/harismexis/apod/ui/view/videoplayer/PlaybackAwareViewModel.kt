package com.harismexis.apod.ui.view.videoplayer

import androidx.lifecycle.ViewModel

abstract class PlaybackAwareViewModel : ViewModel() {

    companion object {
        const val TAG = "PlaybackAwareViewModel"
    }

    private var exoPlayerState: ExoPlayerState = ExoPlayerState()

    fun updateExoPlayerState(state: ExoPlayerState) {
        exoPlayerState = state
    }

    fun getExoPlayerState(id: String): ExoPlayerState {
        if (exoPlayerState.id != id) {
            exoPlayerState = ExoPlayerState(id)
        }
        return exoPlayerState
    }
}