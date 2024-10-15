package com.nghiatd.mixic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BroadcastReceiver(private val listener: SongListener) : BroadcastReceiver() {

    interface SongListener {
        fun onSongStart()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.nghiatd.mixic.SONG_START") {
            listener.onSongStart()
        }
    }
}