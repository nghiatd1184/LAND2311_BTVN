package com.nghiatd.quanlycuahangroom

import android.app.Application
import com.nghiatd.quanlycuahangroom.handler.DatabaseHandler

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        DatabaseHandler.getInstance(this)
    }
}