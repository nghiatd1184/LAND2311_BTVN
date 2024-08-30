package com.example.mymusic.controller

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.mymusic.R
import com.example.mymusic.adapter.SongAdapter
import com.example.mymusic.model.Song
import phucdv.android.musichelper.MediaHelper
import kotlin.concurrent.thread


@Suppress("DEPRECATION")
class Controller private constructor() {
    companion object {
        private var instance: Controller? = null
        fun getInstance(): Controller {
            if (instance == null) {
                instance = Controller()
            }
            return instance!!
        }
    }

    private val songs = arrayListOf<Song>()
    private var mediaPlayer: MediaPlayer? = null
    private var nowPlaying = 0L

    fun checkPermission(context: Context, adapter: SongAdapter) {
        if (context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                999
            )
        } else {
            doRetrieveAllSong(context, adapter)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun doRetrieveAllSong(context: Context, adapter: SongAdapter) {
        thread(start = true) {
            MediaHelper.retrieveAllSong(context) { p0 ->
                if (p0.size > 0) {
                    p0.forEach {
                        songs.add(Song(it.id, it.title, it.artist, it.albumUri))
                    }
                }
                songs.sortBy { it.title }
                Log.d("NGHIA", "doRetrieveAllSong: $songs")
                adapter.notifyDataSetChanged()
            }
        }
    }

    fun getSongs(): List<Song> {
        return songs
    }

    fun songClickHandle(
        context: Context,
        adapter: SongAdapter,
        song: Song,
        position: Int,
        state: ImageView,
        rv: RecyclerView
    ) {
        if (mediaPlayer == null) {
            createMediaPlayer(context, adapter, rv)
        }
        if (nowPlaying == 0L) {
            playSong(context, song)
            nowPlaying = song.id
            song.isPlaying = true
        } else if (nowPlaying == song.id && song.isPlaying) {
            mediaPlayer!!.pause()
            state.setImageResource(R.drawable.btn_pause)
            song.isPlaying = false
        } else if (nowPlaying == song.id) {
            mediaPlayer!!.start()
            state.setImageResource(R.drawable.btn_play)
            song.isPlaying = true
        } else {
            if (mediaPlayer!!.isPlaying) {
                songs.find { it.id == nowPlaying }?.isPlaying = false
                adapter.notifyItemChanged(songs.indexOf(songs.find { it.id == nowPlaying }))
            }
            playSong(context, song)
            nowPlaying = song.id
            song.isPlaying = true
        }

        adapter.notifyItemChanged(position)
    }

    private fun createMediaPlayer(context: Context, adapter: SongAdapter, rv: RecyclerView){
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer!!.setOnPreparedListener {
                mediaPlayer?.start()
            }
            mediaPlayer!!.setOnCompletionListener {
                nextSong(context, adapter, rv)
            }
    }

    private fun playSong(context: Context, song: Song) {
        mediaPlayer!!.reset()
        val trackUri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.id)
        try {
            mediaPlayer!!.setDataSource(context, trackUri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error starting data source", e)
        }
        mediaPlayer!!.prepareAsync()
    }

    fun playPause(context: Context, btn: ImageView, adapter: SongAdapter, rv: RecyclerView) {
        if (mediaPlayer == null) {
            createMediaPlayer(context, adapter, rv)
            playSong(context, songs[0])
            nowPlaying = songs[0].id
            songs[0].isPlaying = true
        } else {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.pause()
                btn.setImageResource(R.drawable.btn_pause)
                songs.find { it.id == nowPlaying }?.isPlaying = false
            } else {
                mediaPlayer!!.start()
                btn.setImageResource(R.drawable.btn_play)
                songs.find { it.id == nowPlaying }?.isPlaying = true
            }
        }
        adapter.notifyItemChanged(songs.indexOf(songs.find { it.id == nowPlaying }))
    }

    fun nextSong(context: Context, adapter: SongAdapter, rv: RecyclerView) {
        if (mediaPlayer == null) return
        val currentSongPosition = songs.indexOf(songs.find { it.id == nowPlaying })
        songs[currentSongPosition].isPlaying = false
        adapter.notifyItemChanged(currentSongPosition)
        val nextSong = if (currentSongPosition < songs.size - 1) {
            songs[currentSongPosition + 1]
        } else {
            songs[0]
        }
        playSong(context, nextSong)
        nowPlaying = nextSong.id
        nextSong.isPlaying = true
        adapter.notifyItemChanged(songs.indexOf(nextSong))
        rv.smoothScrollToPosition(songs.indexOf(nextSong))
    }

    fun previousSong(context: Context, adapter: SongAdapter, rv: RecyclerView) {
        if (mediaPlayer == null) return
        val currentSongPosition = songs.indexOf(songs.find { it.id == nowPlaying })
        songs[currentSongPosition].isPlaying = false
        adapter.notifyItemChanged(currentSongPosition)
        val previousSong = if (currentSongPosition > 0) {
            songs[currentSongPosition - 1]
        } else {
            songs[songs.size - 1]
        }
        playSong(context, previousSong)
        nowPlaying = previousSong.id
        previousSong.isPlaying = true
        adapter.notifyItemChanged(songs.indexOf(previousSong))
        rv.smoothScrollToPosition(songs.indexOf(previousSong))
    }

    fun releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }
}