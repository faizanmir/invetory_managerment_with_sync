package com.fx.inventory.application

import android.app.Application
import androidx.work.Configuration
import com.fx.inventory.sync.AppWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class App:Application(), Configuration.Provider {
    @Inject
    lateinit var appWorkerFactory:AppWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
       return Configuration.Builder()
            .setWorkerFactory(appWorkerFactory)
            .build()
    }
}