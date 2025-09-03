package com.example.moneyflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for MoneyFlow
 * Required for Dagger Hilt initialization
 */
@HiltAndroidApp
class MoneyFlowApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-wide components here
    }
}
