package com.rayxaus.reeltusky

import android.app.Application
import com.rayxaus.reeltusky.di.AppContainer

class ReelTuskyApplication : Application() {
    lateinit var appContainer: AppContainer private set

    override fun onCreate() {
        super.onCreate()
        appContainer = AppContainer(applicationContext)
    }
}