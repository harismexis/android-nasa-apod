package com.harismexis.apod.app

import com.harismexis.apod.data.remote.RetrofitApiFactory
import com.harismexis.apod.data.remote.ApodApi
import com.harismexis.apod.data.database.DatabaseFactory
import com.harismexis.apod.data.repository.ApodRepository
import com.harismexis.apod.data.repository.LocalRepository

class DependencyContainer(application: MainApplication) {
    val api: ApodApi = RetrofitApiFactory.api
    val dao = DatabaseFactory.getDao(application.applicationContext)
    val apodRepository: ApodRepository = ApodRepository(api, dao)
    val localRepository: LocalRepository = LocalRepository(dao)
}
