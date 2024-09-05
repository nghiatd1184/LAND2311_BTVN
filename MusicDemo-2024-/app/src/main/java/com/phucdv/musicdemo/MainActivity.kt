package com.phucdv.musicdemo

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioAttributes.CONTENT_TYPE_MUSIC
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.phucdv.musicdemo.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnSongClickListener {
    private val viewModel by viewModels<MainActivityViewModel> {
        MainActivityViewModel.Factory(this)
    }
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { requireNotNull(_binding) }
    private val songs: MutableList<Song> = mutableListOf()
    private lateinit var mediaPlayer: MediaPlayer
    private var nowPlaying: Long = 0L
    private val songAdapter by lazy { SongAdapter(songs, this) }

    private val isAtLeast13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val permission = if (isAtLeast13) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalIfNeed()
        } else {
            listenViewModel()
        }
        createMediaPlayer()
        binding.apply {
            btnPlayPause.setOnClickListener {
                playPause()
            }
            btnNext.setOnClickListener {
                nextSong()
            }
            btnPrevious.setOnClickListener {
                previousSong()
            }
            rvSong.apply {
                adapter = songAdapter
                layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewModel.allSongs.collectLatest { allSongs ->
                songs.clear()
                songs.addAll(allSongs)
                songAdapter.notifyItemRangeInserted(0, allSongs.size)
            }
        }
    }

    private fun createMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer.setAudioAttributes(AudioAttributes.Builder().setContentType(CONTENT_TYPE_MUSIC).build())
        mediaPlayer.setOnPreparedListener {
            it.start()
        }
        mediaPlayer.setOnCompletionListener {
            nextSong()
        }
    }

    private fun playSong(song: Song) {
        mediaPlayer.reset()
        val trackUri =
            ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, song.id)
        try {
            mediaPlayer.setDataSource(this, trackUri)
        } catch (e: Exception) {
            Log.e("MUSIC SERVICE", "Error starting data source", e)
        }
        mediaPlayer.prepareAsync()
    }

    private fun playPause() {
        if (!mediaPlayer.isPlaying) {
            playSong(songs[0])
            nowPlaying = songs[0].id
            songs[0].isPlaying = true
        } else {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.btnPlayPause.setImageResource(R.drawable.btn_pause)
                songs.find { it.id == nowPlaying }?.isPlaying = false
            } else {
                mediaPlayer.start()
                binding.btnPlayPause.setImageResource(R.drawable.btn_play)
                songs.find { it.id == nowPlaying }?.isPlaying = true
            }
        }
        songAdapter.notifyItemChanged(songs.indexOf(songs.find { it.id == nowPlaying }))
    }

    private fun nextSong() {
        if (!mediaPlayer.isPlaying) return
        val currentSongPosition = songs.indexOf(songs.find { it.id == nowPlaying })
        songs[currentSongPosition].isPlaying = false
        songAdapter.notifyItemChanged(currentSongPosition)
        val nextSong = if (currentSongPosition < songs.size - 1) {
            songs[currentSongPosition + 1]
        } else {
            songs[0]
        }
        playSong(nextSong)
        nowPlaying = nextSong.id
        nextSong.isPlaying = true
        songAdapter.notifyItemChanged(songs.indexOf(nextSong))
        binding.rvSong.smoothScrollToPosition(songs.indexOf(nextSong))
    }

    private fun previousSong() {
        if (!mediaPlayer.isPlaying) return
        val currentSongPosition = songs.indexOf(songs.find { it.id == nowPlaying })
        songs[currentSongPosition].isPlaying = false
        songAdapter.notifyItemChanged(currentSongPosition)
        val previousSong = if (currentSongPosition > 0) {
            songs[currentSongPosition - 1]
        } else {
            songs[songs.size - 1]
        }
        playSong(previousSong)
        nowPlaying = previousSong.id
        previousSong.isPlaying = true
        songAdapter.notifyItemChanged(songs.indexOf(previousSong))
        binding.rvSong.smoothScrollToPosition(songs.indexOf(previousSong))
    }

    private val requestReadExternalPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                listenViewModel()
            } else {
                showPermissionDialog()
            }
        }

    private fun openAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            addCategory(CATEGORY_DEFAULT)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_HISTORY)
            addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

        startActivity(intent)
    }

    private fun requestReadExternalIfNeed() {
        when {
            checkSelfPermission(permission)
                    == PackageManager.PERMISSION_GRANTED -> {
                listenViewModel()
            }

            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionDialog()
            }

            else -> {
                requestReadExternalPermissionLauncher.launch(
                    permission
                )
            }
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(this).apply {
            setTitle("Cảnh báo")
            setCancelable(false)
            setMessage("Ứng dụng cần truy cập vào bộ nhớ để phát nhạc. Vui lòng cấp quyền và khởi động lại ứng dụng!")
            setPositiveButton("Cấp Quyền") { _, _ ->
                finish()
                openAppSettings()
            }
            setNegativeButton("Thoát") { _, _ ->
                finish()
            }
        }.show()
    }

    override fun onSongClick(song: Song, position: Int) {
        if (nowPlaying == 0L) {
            playSong(song)
            nowPlaying = song.id
            song.isPlaying = true
        } else if (nowPlaying == song.id && song.isPlaying) {
            mediaPlayer.pause()
            binding.btnPlayPause.setImageResource(R.drawable.btn_pause)
            song.isPlaying = false
        } else if (nowPlaying == song.id) {
            mediaPlayer.start()
            binding.btnPlayPause.setImageResource(R.drawable.btn_play)
            song.isPlaying = true
        } else {
            if (mediaPlayer.isPlaying) {
                songs.find { it.id == nowPlaying }?.isPlaying = false
                songAdapter.notifyItemChanged(songs.indexOf(songs.find { it.id == nowPlaying }))
            }
            playSong(song)
            nowPlaying = song.id
            song.isPlaying = true
        }

        songAdapter.notifyItemChanged(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        mediaPlayer.release()
    }
}