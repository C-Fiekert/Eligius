package com.callum.eligius.main

import android.app.Application
import com.callum.eligius.models.PortfolioMemStore
import com.callum.eligius.models.PortfolioStore
import timber.log.Timber

class Main : Application() {

    lateinit var portfoliosStore: PortfolioStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        portfoliosStore = PortfolioMemStore()
        Timber.i("Eligius Application Started")
    }
}