package com.example.camelan_nearby_assign

import android.app.Application
import com.example.camelan_nearby_assign.di.DaggerApplicationComponent
import timber.log.Timber

class MyApp: Application() {

    val appComponent = DaggerApplicationComponent.create()

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}