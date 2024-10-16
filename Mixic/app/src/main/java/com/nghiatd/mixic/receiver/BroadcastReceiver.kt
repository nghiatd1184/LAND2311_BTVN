package com.nghiatd.mixic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nghiatd.mixic.MyApplication
import com.nghiatd.mixic.service.MusicService

class BroadcastReceiver(private val listener: SongListener) : BroadcastReceiver() {

    interface SongListener {
        fun onSongStart()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == MyApplication.ACTION_SONG_START) {
            listener.onSongStart()
        }
    }
}