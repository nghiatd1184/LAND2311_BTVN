package com.nghiatd.mixic

import android.app.Service
import android.content.ContentUris
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import com.nghiatd.mixic.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

class MusicService(private val songList: List<Song>) : Service() {
    companion object {
        const val ACTION_PLAY_PAUSE = "ACTION_PLAY_PAUSE"
        const val EXTRA_SONG_ID = "extra_song_id" // Long

        const val ACTION_NEXT = "ACTION_NEXT"
        const val ACTION_PREV = "ACTION_PREV"
        const val ACTION_SEEK = "ACTION_SEEK"
        const val EXTRA_SEEK_VALUE = "extra_seek_value" // Long

        const val ACTION_REPEAT = "ACTION_REPEAT"
        const val EXTRA_REPEAT_MODE = "extra_repeat_mode" // Int (0, 1, 2)
        const val REPEAT_MODE_OFF = 0
        const val REPEAT_MODE_ONE = 1
        const val REPEAT_MODE_ALL = 2

        const val ACTION_SHUFFLE = "ACTION_SHUFFLE"
        const val EXTRA_SHUFFLE_MODE = "extra_shuffle_mode" // Boolean
    }

    private val scope = CoroutineScope(Dispatchers.Main)
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs = _allSongs.asStateFlow()
    val currentPlaying = MutableStateFlow<Pair<Int, Song>?>(null)
    val isPlayingFlow = MutableStateFlow(false)

    val repeatMode = MutableStateFlow<Int>(REPEAT_MODE_OFF)
    val isShuffle = MutableStateFlow<Boolean>(false)

    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener {
            start()
        }
        setOnCompletionListener {
            playOnEnd()
        }
    }

    fun setPlayList(list: List<Song>) {
        scope.launch {
            _allSongs.value = list
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_PAUSE -> {
                val songId = intent.getLongExtra(EXTRA_SONG_ID, -1L)
                val song = allSongs.value.find { it.id == songId }
                if (song != null) {
                    playPause(song)
                }
            }

            ACTION_NEXT -> {
                playNext()
            }

            ACTION_PREV -> {
                playPrev()
            }

            ACTION_SEEK -> {
                val seekValue = intent.getLongExtra(EXTRA_SEEK_VALUE, -1L)
                if (seekValue != -1L) {
                    seekTo(seekValue)
                }
            }

            ACTION_REPEAT -> {
                val repeatMode = intent.getIntExtra(EXTRA_REPEAT_MODE, -1)
                if (repeatMode != -1) {
                    setRepeatMode(repeatMode)
                }
            }

            ACTION_SHUFFLE -> {
                val shuffleMode = intent.getBooleanExtra(EXTRA_SHUFFLE_MODE, false)
                setShuffleMode(shuffleMode)
            }

            else -> {
                throw IllegalArgumentException("Unsupported action ${intent?.action}")
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun setShuffleMode(shuffleMode: Boolean) {
        this.isShuffle.value = shuffleMode
    }

    private fun setRepeatMode(repeatMode: Int) {
        this.repeatMode.value = repeatMode
    }

    private fun seekTo(seekValue: Long) {
        mediaPlayer.seekTo(seekValue.toInt())
    }

    private fun playOnEnd() {
        val repeatMode = repeatMode.value
        val listSong = allSongs.value
        when (repeatMode) {
            REPEAT_MODE_OFF -> {
                if (currentPlaying.value?.first == listSong.size - 1) {
                    isPlayingFlow.value = false
                } else playNext()
            }

            REPEAT_MODE_ONE -> {
                val currentPlaying = currentPlaying.value
                currentPlaying?.let {
                    playSong(it.second)
                }
            }

            REPEAT_MODE_ALL -> {
                playNext()
            }
        }
    }

    private fun playPrev() {
        val listSong = allSongs.value
        if (listSong.isEmpty()) return

        val currentPlaying = currentPlaying.value

        val song = if (currentPlaying == null) {
            listSong[listSong.size - 1]
        } else if (currentPlaying.first == 0) {
            listSong[listSong.size - 1]
        } else {
            listSong[(currentPlaying.first ?: 1) - 1]
        }
        playSong(song)
    }

    private fun playNext() {
        val listSong = allSongs.value
        if (listSong.isEmpty()) return

        val isShuffle = isShuffle.value
        val currentPlaying = currentPlaying.value

        val song = if (isShuffle) {
            val index = Random(listSong.size - 1).nextInt()
            listSong[index]
        } else {
            if (currentPlaying == null) {
                listSong[0]
            } else if (currentPlaying.first == listSong.size - 1) {
                listSong[0]
            } else {
                listSong[(currentPlaying.first ?: -1) + 1]
            }
        }
        playSong(song)
    }

    private fun playPause(song: Song) {
        if (currentPlaying.value?.second == song) {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                isPlayingFlow.value = false
            } else {
                mediaPlayer.start()
                isPlayingFlow.value = true
            }
        } else {
            playSong(song)
        }
    }

    private fun playSong(song: Song) {
        isPlayingFlow.value = true
        val listSong = allSongs.value
        mediaPlayer.reset()
        val index = listSong.indexOf(song)
        currentPlaying.value = index to song
        try {
            mediaPlayer.setDataSource(song.data)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error starting data source", e)
        }
        mediaPlayer.prepareAsync()
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }

    inner class MusicBinder : Binder() {
        fun getMusicService(): MusicService {
            return this@MusicService
        }
    }
}