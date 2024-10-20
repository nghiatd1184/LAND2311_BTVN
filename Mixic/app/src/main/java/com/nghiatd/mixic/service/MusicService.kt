package com.nghiatd.mixic.service


import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaPlayer
import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.MainActivity
import com.nghiatd.mixic.MyApplication
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.receiver.BroadcastReceiver
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

class MusicService : Service() {
    companion object {
        const val REPEAT_MODE_OFF = 0
        const val REPEAT_MODE_ONE = 1
        const val REPEAT_MODE_ALL = 2
    }

    private val scope = CoroutineScope(Dispatchers.Main)
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs = _allSongs.asStateFlow()
    val currentPlaying = MutableStateFlow<Song?>(null)
    val isPlayingFlow = MutableStateFlow(false)
    val repeatMode = MutableStateFlow(REPEAT_MODE_OFF)
    val isShuffle = MutableStateFlow(false)
    val isMute = MutableStateFlow(false)
    private lateinit var equalizer: Equalizer
    private lateinit var bassBoost: BassBoost
    private lateinit var virtualizer: Virtualizer
    private lateinit var mediaSessionCompat: MediaSessionCompat

    private val mediaPlayer = MediaPlayer().apply {
        setOnPreparedListener {
            start()
            onSongStartBroadcast()
            scope.launch {
                isPlayingFlow.collectLatest {
                    val playbackSpeed = if (it) 1f else 0f
                    val playbackState =
                        if (it) PlaybackStateCompat.STATE_PLAYING else PlaybackStateCompat.STATE_PAUSED
                    val btnPlayPause = if (it) R.drawable.icon_pause else R.drawable.icon_play
                    val duration = duration.toLong()
                    showNotification(btnPlayPause, playbackState, playbackSpeed, duration)
                }
            }
        }
        setOnCompletionListener {
            playOnEnd()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val audioSessionId = getAudioSessionId()
        mediaSessionCompat = MediaSessionCompat(baseContext, "Mixic")
        equalizer = Equalizer(0, audioSessionId).apply { enabled = true }
        bassBoost = BassBoost(0, audioSessionId).apply { enabled = true }
        virtualizer = Virtualizer(0, audioSessionId).apply { enabled = true }

        val sharedPreferences = getSharedPreferences("mixic_data", Context.MODE_PRIVATE)
        val minEQLevel = equalizer.bandLevelRange[0]
        val maxEQLevel = equalizer.bandLevelRange[1]

        for (i in 0 until equalizer.numberOfBands) {
            val bandIndex = i.toShort()
            val savedLevel =
                sharedPreferences.getInt("BandLevel_$i", equalizer.getBandLevel(bandIndex).toInt())
            val adjustedLevel = savedLevel.coerceIn(minEQLevel.toInt(), maxEQLevel.toInt())
            equalizer.setBandLevel(bandIndex, adjustedLevel.toShort())
        }

        val bassBoostStrength =
            sharedPreferences.getInt("BassBoostStrength", bassBoost.roundedStrength.toInt())
        bassBoost.setStrength(bassBoostStrength.toShort())

        val virtualizerStrength =
            sharedPreferences.getInt("VirtualizerStrength", virtualizer.roundedStrength.toInt())
        virtualizer.setStrength(virtualizerStrength.toShort())

        val isEqualizerEnabled = sharedPreferences.getBoolean("EqualizerState", false)
        equalizer.enabled = isEqualizerEnabled
        bassBoost.enabled = isEqualizerEnabled
        virtualizer.enabled = isEqualizerEnabled

        isShuffle.value = sharedPreferences.getBoolean("ShuffleMode", false)
        repeatMode.value = sharedPreferences.getInt("RepeatMode", REPEAT_MODE_OFF)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            MyApplication.ACTION_NEXT -> playNext()
            MyApplication.ACTION_PREVIOUS -> playPrev()
            MyApplication.ACTION_PLAY_PAUSE -> playPause(currentPlaying.value)
        }
        return START_STICKY
    }

    fun setShuffleMode(shuffleMode: Boolean) {
        this.isShuffle.value = shuffleMode
        val sharedPreferences = getSharedPreferences("mixic_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("ShuffleMode", shuffleMode)
        editor.apply()
    }

    fun setRepeatMode(repeatMode: Int) {
        this.repeatMode.value = repeatMode
        val sharedPreferences = getSharedPreferences("mixic_data", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("RepeatMode", repeatMode)
        editor.apply()
    }

    fun seekTo(seekValue: Long) {
        mediaPlayer.seekTo(seekValue.toInt())
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun toggleMute() {
        isMute.value = !isMute.value
        if (isMute.value) {
            mediaPlayer.setVolume(0f, 0f)
        } else {
            mediaPlayer.setVolume(1f, 1f)
        }
    }

    fun getAudioSessionId(): Int {
        return mediaPlayer.audioSessionId
    }

    private fun playOnEnd() {
        val repeatMode = repeatMode.value
        val listSong = allSongs.value
        when (repeatMode) {
            REPEAT_MODE_OFF -> {
                val currentPlayingIndex = listSong.indexOf(currentPlaying.value)
                if (currentPlayingIndex == listSong.size - 1) {
                    isPlayingFlow.value = false
                } else playNext()
            }

            REPEAT_MODE_ONE -> {
                currentPlaying.value?.let { playSong(it) }
            }

            REPEAT_MODE_ALL -> {
                playNext()
            }
        }
    }

    fun playPrev() {
        val listSong = allSongs.value
        if (listSong.isEmpty()) return

        val currentPlayingIndex = listSong.indexOf(currentPlaying.value)

        val song = if (currentPlayingIndex == -1 || currentPlayingIndex == 0) {
            listSong[listSong.size - 1]
        } else {
            listSong[currentPlayingIndex - 1]
        }
        playSong(song)
    }

    fun playNext() {
        val listSong = allSongs.value
        if (listSong.size <= 1) return

        val isShuffle = isShuffle.value
        val currentPlayingIndex = listSong.indexOf(currentPlaying.value)

        val song = if (isShuffle) {
            val index = Random.nextInt(0, listSong.size - 1)
            listSong[index]
        } else {
            if (currentPlayingIndex == -1 || currentPlayingIndex == listSong.size - 1) {
                listSong[0]
            } else {
                listSong[currentPlayingIndex + 1]
            }
        }
        playSong(song)
    }

    fun playPause(song: Song?) {
        if (allSongs.value.isEmpty()) {
            Toast.makeText(this@MusicService, "Please pick a song first!", Toast.LENGTH_SHORT)
                .show()
            return
        }

        if (song == null) {
            playSong(allSongs.value[0])
            return
        }
        if (currentPlaying.value == song) {
            if (mediaPlayer.isPlaying) {
                pause()
                isPlayingFlow.value = false
            } else {
                resume()
                isPlayingFlow.value = true
            }
        } else {
            playSong(song)
        }
    }

    fun pause() {
        mediaPlayer.pause()
    }

    fun resume() {
        mediaPlayer.start()
    }

    private fun playSong(song: Song) {
        isPlayingFlow.value = true
        val listSong = allSongs.value
        mediaPlayer.reset()
        val index = listSong.indexOf(song)
        currentPlaying.value = listSong[index]
        try {
            mediaPlayer.setDataSource(song.data)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error starting data source", e)
        }
        mediaPlayer.prepareAsync()
    }

    fun setPlayList(list: List<Song>) {
        _allSongs.value = list
    }

    fun getDuration(): Int {
        return mediaPlayer.duration
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()
        equalizer.release()
        bassBoost.release()
        virtualizer.release()
    }

    override fun onBind(intent: Intent?): IBinder {
        return MusicBinder()
    }

    inner class MusicBinder : Binder() {
        fun getMusicService(): MusicService {
            return this@MusicService
        }
    }

    private fun onSongStartBroadcast() {
        val intent = Intent(MyApplication.ACTION_SONG_START)
        sendBroadcast(intent)
    }

    private suspend fun showNotification(
        btnPlayPause: Int,
        playbackState: Int,
        playbackSpeed: Float,
        duration: Long
    ) {
        val song = currentPlaying.value
        val contentIntent = Intent(baseContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val pendingContentIntent = PendingIntent.getActivity(
            baseContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val prevIntent = Intent(baseContext, BroadcastReceiver::class.java)
            .setAction(MyApplication.ACTION_PREVIOUS)
        val prevPending = PendingIntent.getBroadcast(
            baseContext,
            0,
            prevIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val nextIntent = Intent(baseContext, BroadcastReceiver::class.java)
            .setAction(MyApplication.ACTION_NEXT)
        val nextPending = PendingIntent.getBroadcast(
            baseContext,
            0,
            nextIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val playPauseIntent = Intent(baseContext, BroadcastReceiver::class.java)
            .setAction(MyApplication.ACTION_PLAY_PAUSE)
        val playPausePending = PendingIntent.getBroadcast(
            baseContext,
            0,
            playPauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val picture = song?.image
        val thumb: Bitmap = withContext(Dispatchers.IO) {
            try {
                Glide.with(this@MusicService)
                    .asBitmap()
                    .load(picture)
                    .apply(RequestOptions().transform(BlurTransformation(5, 1)))
                    .submit()
                    .get()
            } catch (e: Exception) {
                Glide.with(this@MusicService)
                    .asBitmap()
                    .load(R.drawable.splash_img)
                    .apply(RequestOptions().transform(BlurTransformation(5, 1)))
                    .submit()
                    .get()
            }
        }

        val notification = NotificationCompat.Builder(baseContext, MyApplication.CHANNEL_ID_1)
            .setSmallIcon(R.drawable.logo_notification)
            .setContentTitle(song?.name ?: getString(R.string.app_name))
            .setContentText(song?.artist ?: "Mixic is running")
            .setLargeIcon(thumb)
            .addAction(R.drawable.icon_previous, "Previous", prevPending)
            .addAction(btnPlayPause, "Play/Pause", playPausePending)
            .addAction(R.drawable.icon_next, "Next", nextPending)
            .setStyle(MediaStyle().setMediaSession(mediaSessionCompat.sessionToken))
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingContentIntent)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()

        mediaSessionCompat.setMetadata(
            MediaMetadataCompat.Builder()
                .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                .build()
        )
        mediaSessionCompat.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(playbackState, getCurrentPosition().toLong(), playbackSpeed)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO or PlaybackStateCompat.ACTION_PLAY_PAUSE or PlaybackStateCompat.ACTION_SKIP_TO_NEXT or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
                .build()
        )

        mediaSessionCompat.setCallback(object : MediaSessionCompat.Callback() {
            override fun onPlay() {
                super.onPlay()
                playPause(song)
            }

            override fun onPause() {
                super.onPause()
                playPause(song)
            }

            override fun onSkipToNext() {
                super.onSkipToNext()
                playNext()
            }

            override fun onSkipToPrevious() {
                super.onSkipToPrevious()
                playPrev()
            }

            override fun onSeekTo(pos: Long) {
                super.onSeekTo(pos)
                seekTo(pos)
            }
        })

        startForeground(1, notification)
    }
}