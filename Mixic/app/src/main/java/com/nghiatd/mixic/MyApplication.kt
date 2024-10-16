package com.nghiatd.mixic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID_1 = "music_service_channel"
        const val ACTION_PLAY_PAUSE = "action_play_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
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