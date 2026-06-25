package com.harismexis.apod.app

import android.app.Application

class MainApplication : Application() {
    val dependencies by lazy { DependencyContainer(this)}
}

val Application.dependencies: DependencyContainer
    get() = (this as MainApplication).dependencies