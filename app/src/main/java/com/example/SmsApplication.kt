package com.example

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.WorkManager

class SmsApplication : Application(), Configuration.Provider {

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setMinimumLoggingLevel(Log.INFO)
            .build()

    override fun onCreate() {
        super.onCreate()
        try {
            if (!WorkManager.isInitialized()) {
                WorkManager.initialize(this, workManagerConfiguration)
            }
        } catch (e: Exception) {
            Log.w("SmsApplication", "WorkManager already initialized or auto-initialized: ${e.message}")
        }
        Log.i("SmsApplication", "SMS Forwarder Pro Application initialized")
    }
}

