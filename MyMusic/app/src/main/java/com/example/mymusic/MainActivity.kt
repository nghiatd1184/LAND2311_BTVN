package com.example.mymusic

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymusic.adapter.SongAdapter
import com.example.mymusic.controller.Controller
import com.example.mymusic.databinding.ActivityMainBinding
import com.example.mymusic.listener.OnSongClickListener
import com.example.mymusic.model.Song


class MainActivity : AppCompatActivity(), OnSongClickListener {
    private var _binding: ActivityMainBinding? = null
    private val binding: ActivityMainBinding by lazy { requireNotNull(_binding) }
    private val controller by lazy { Controller.getInstance() }
    private val adapter: SongAdapter by lazy { SongAdapter(controller.getSongs(), this) }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        controller.checkPermission(this, adapter)
        binding.apply {
            btnPlayPause.setOnClickListener {
                controller.playPause(this@MainActivity, btnPlayPause, adapter, binding.rvSong)
            }
            btnNext.setOnClickListener {
                controller.nextSong(this@MainActivity, adapter, binding.rvSong)
            }
            btnPrevious.setOnClickListener {
                controller.previousSong(this@MainActivity, adapter, binding.rvSong)
            }
        }
        binding.rvSong.apply {
            adapter = this@MainActivity.adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 999) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                controller.doRetrieveAllSong(this, adapter)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        controller.releaseMediaPlayer()
    }

    override fun onSongClick(song: Song, position: Int) {
        controller.songClickHandle(
            this,
            adapter,
            song,
            position,
            binding.btnPlayPause,
            binding.rvSong
        )
    }
}