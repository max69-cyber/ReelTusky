package com.rayxaus.reeltusky

import android.app.Application
import com.rayxaus.reeltusky.di.AppContainer

class ReelTuskyApplication : Application() {
    val appContainer: AppContainer = AppContainer()
}