package com.nghiatd.mixic.ui.home

import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.DeviceSongAdapter
import com.nghiatd.mixic.data.viewmodel.SongViewModel
import com.nghiatd.mixic.databinding.FragmentDeviceBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceFragment : Fragment() {

    private lateinit var binding: FragmentDeviceBinding
    private lateinit var viewModel: SongViewModel

    private val minimizedLayout = parentFragment?.view?.findViewById<View>(R.id.minimized_layout)
    private val btnPlayPause = minimizedLayout?.findViewById<ImageView>(R.id.btn_play_pause)
    private val btnNext = minimizedLayout?.findViewById<View>(R.id.btn_next)
    private val btnPrev = minimizedLayout?.findViewById<View>(R.id.btn_previous)

    private var service: MusicService? = null
    private var isBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            this@DeviceFragment.service = binder.getMusicService()
            isBound = true
            listenViewModel()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    private val deviceSongAdapter = DeviceSongAdapter { song ->
        requireActivity().startService(Intent(requireContext(), MusicService::class.java).apply {
            action = MusicService.ACTION_PLAY_PAUSE
            putExtra(MusicService.EXTRA_SONG_ID, song.id)
        })
        val imgArtUri = Uri.parse(song.image)
        val imgThumb = (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)?.findViewById<ImageView>(R.id.img_thumb)
        val tvSongName = (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)?.findViewById<TextView>(R.id.tv_name)
        val tvArtist = (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)?.findViewById<TextView>(R.id.tv_artist)
        Glide.with(imgThumb!!)
            .load(imgArtUri)
            .apply(RequestOptions().transform(RoundedCorners(15)))
            .into(imgThumb)
        tvSongName!!.text = song.name
        tvArtist!!.text = song.artist
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModeFactory = SongViewModel.SongViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModeFactory)[SongViewModel::class.java]
        binding = FragmentDeviceBinding.inflate(inflater, container, false)
        requireActivity().bindService(Intent(requireActivity(), MusicService::class.java), serviceConnection, BIND_AUTO_CREATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.recyclerViewDeviceSong.apply {
            adapter = deviceSongAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        btnPlayPause?.setOnClickListener {
            requireContext().startService(
                Intent(
                    requireContext(),
                    MusicService::class.java
                ).apply {
                    action = MusicService.ACTION_PLAY_PAUSE
                    putExtra(
                        MusicService.EXTRA_SONG_ID,
                        service?.currentPlaying?.value?.second?.id
                    )
                })
        }

        btnNext?.setOnClickListener {
            requireContext().startService(
                Intent(
                    requireContext(),
                    MusicService::class.java
                ).apply {
                    action = MusicService.ACTION_NEXT
                })
        }

        btnPrev?.setOnClickListener {
            requireContext().startService(
                Intent(
                    requireContext(),
                    MusicService::class.java
                ).apply {
                    action = MusicService.ACTION_PREV
                })
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewModel.allSongs.collectLatest { allSongs ->
                deviceSongAdapter.submitList(allSongs)
                service?.setPlayList(allSongs)
            }
        }

        lifecycleScope.launch {
            service?.isPlayingFlow?.collectLatest { isPlaying ->
                deviceSongAdapter.isPlaying = isPlaying
                val imgPlayPause = if (isPlaying) R.drawable.icon_pause else R.drawable.icon_play
                val btn = (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)?.findViewById<ImageView>(R.id.btn_play_pause)
                Glide.with(btn!!)
                    .load(imgPlayPause)
                    .into(btn)
            }
        }

        lifecycleScope.launch {
            service?.currentPlaying?.collectLatest { currentPlaying ->
                deviceSongAdapter.playingSong = currentPlaying?.second
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireContext().unbindService(serviceConnection)
            isBound = false
        }
    }

}


