package com.example.cardflip.controller

import android.content.Context
import android.media.MediaPlayer
import com.example.cardflip.R

class MusicController private constructor() {

    companion object {
        private var instance: MusicController? = null
        fun getInstance(): MusicController {
            if (instance == null) {
                instance = MusicController()
            }
            return instance!!
        }
    }

    private var mediaPlayer: MediaPlayer? = null

    fun play(context: Context) {
        if (mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, R.raw.bgm)
            mediaPlayer!!.isLooping = true
            mediaPlayer!!.start()
        } else mediaPlayer!!.start()
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun resume() {
        try {
            mediaPlayer?.start()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun stop() {
        try {
            mediaPlayer?.stop()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun release() {
        try {
            mediaPlayer?.release()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
}