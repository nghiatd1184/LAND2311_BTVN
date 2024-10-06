package com.nghiatd.mixic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class OnSongCompletionReceiver(private val listener: SongCompletionListener) : BroadcastReceiver() {

    interface SongCompletionListener {
        fun onSongCompletion()
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "com.nghiatd.mixic.SONG_COMPLETED") {
            listener.onSongCompletion()
        }
    }
}