package com.nghiatd.mixic

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log

class MyApplication : Application() {

    companion object {
        const val CHANNEL_ID_1 = "music_service_channel_1"
        const val ACTION_SONG_START = "com.nghiatd.mixic.SONG_START"
        const val CLOUD_TYPE = "song frome cloud"
        const val DEVICE_TYPE = "song from device"
        const val ACTION_PLAY_PAUSE = "action_play_pause"
        const val ACTION_NEXT = "action_next"
        const val ACTION_PREVIOUS = "action_previous"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel1 = NotificationChannel(CHANNEL_ID_1, "Music Service Channel 1", NotificationManager.IMPORTANCE_HIGH)
        channel1.description = "Music Service Channel 1"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel1)
    }
}