package com.nghiatd.mixic.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.nghiatd.mixic.MyApplication
import com.nghiatd.mixic.service.MusicService

class BroadcastReceiver() : BroadcastReceiver() {

    private var listener: SongListener? = null

    interface SongListener {
        fun onSongStart()
    }

    constructor(listener: SongListener) : this() {
        this.listener = listener
    }

    fun setSongListener(listener: SongListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val actionName = intent?.action
        val serviceIntent = Intent(context, MusicService::class.java)
        if (actionName != null) {
            when (actionName) {
                MyApplication.ACTION_SONG_START -> {
                    listener?.onSongStart()
                }
                else -> {
                    context?.startService(serviceIntent.apply {
                        action = actionName
                    })
                }
            }
        }
    }
}