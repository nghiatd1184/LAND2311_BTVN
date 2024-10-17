package com.nghiatd.mixic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.util.Log

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID_1 = "music_service_channel_1"
        const val ACTION_SONG_START = "com.nghiatd.mixic.SONG_START"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel1 = NotificationChannel(CHANNEL_ID_1, "Music Service Channel 1", NotificationManager.IMPORTANCE_HIGH)
        channel1.description = "Music Service Channel 1"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel1)
    }
}