package com.nghiatd.mixic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID_1 = "music_service_channel"
        const val ACTION_SONG_START = "com.nghiatd.mixic.SONG_START"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel1 = NotificationChannel(CHANNEL_ID_1, "Music Service Channel", NotificationManager.IMPORTANCE_HIGH)
        channel1.description = "Music Service Channel"
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel1)
    }
}