package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.SongAdapter
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.viewmodel.DeviceViewModel
import com.nghiatd.mixic.databinding.FragmentDeviceBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DeviceFragment : Fragment() {

    private lateinit var binding: FragmentDeviceBinding
    private lateinit var viewModel: DeviceViewModel
    private var service: MusicService? = null
    private var servicePlayList: MutableList<Song> = emptyList<Song>().toMutableList()

    private val songAdapter: SongAdapter by lazy {
        SongAdapter { song ->
            if (servicePlayList != songAdapter.currentList) {
                service?.setPlayList(songAdapter.currentList)
            }
            service?.playPause(song)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModeFactory = DeviceViewModel.SongViewModelFactory(requireContext())
        viewModel = ViewModelProvider(this, viewModeFactory)[DeviceViewModel::class.java]
        service = (parentFragment as HomeFragment).getMusicService()
        listenViewModel()
        binding = FragmentDeviceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.recyclerViewDeviceSong.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewModel.allSongs.collectLatest { allSongs ->
                songAdapter.submitList(allSongs)
            }
        }

        lifecycleScope.launch {
            service?.isPlayingFlow?.collectLatest { isPlaying ->
                songAdapter.isPlaying = isPlaying
                val imgPlayPause = if (isPlaying) R.drawable.icon_pause else R.drawable.icon_play
                val minimizedPlayPauseBtn =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)
                        ?.findViewById<ImageView>(R.id.btn_play_pause)
                Glide.with(minimizedPlayPauseBtn!!)
                    .load(imgPlayPause)
                    .into(minimizedPlayPauseBtn)
            }
        }

        lifecycleScope.launch {
            service?.currentPlaying?.collectLatest { currentPlaying ->
                songAdapter.playingSong = currentPlaying
            }
        }

        lifecycleScope.launch {
            service?.allSongs?.collectLatest {
                this@DeviceFragment.servicePlayList.clear()
                this@DeviceFragment.servicePlayList.addAll(it)
            }
        }
    }

}


