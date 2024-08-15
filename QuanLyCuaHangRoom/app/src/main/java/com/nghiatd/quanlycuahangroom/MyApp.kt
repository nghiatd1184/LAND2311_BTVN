package com.nghiatd.quanlycuahangroom

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.nghiatd.quanlycuahangroom.handler.DatabaseHandler

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseHandler.getInstance(this)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }
}