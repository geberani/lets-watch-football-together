package com.ranicorp.letswatchfootballtogether

import android.app.Application
import com.ranicorp.letswatchfootballtogether.data.source.PreferenceManager

class FootballApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        preferencesManager = PreferenceManager(this)
        appContainer = AppContainer()
    }

    companion object {
        lateinit var preferencesManager: PreferenceManager
        lateinit var appContainer: AppContainer
    }
}