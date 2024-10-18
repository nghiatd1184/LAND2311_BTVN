package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.mixic.MyApplication
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.PlaylistAdapter
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentPlaylistBinding
import com.nghiatd.mixic.data.viewmodel.PlayListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playListViewModel: PlayListViewModel
    private lateinit var sharedDataViewModel: SharedDataViewModel
    private var type : String = MyApplication.CLOUD_TYPE
    private var playlistOnDevice: MutableList<Playlist> = mutableListOf()
    private var playlistOnCloud: MutableList<Playlist> = mutableListOf()
    private val _playlistSource = MutableStateFlow<List<Playlist>>(emptyList())
    private val playlistSource = _playlistSource.asStateFlow()

    private val playlistAdapter: PlaylistAdapter by lazy {
        PlaylistAdapter(playListViewModel) { playlist ->
            sharedDataViewModel.setCategory(Category(playlist.id, playlist.name, playlist.songs))
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, SongListFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModeFactory = PlayListViewModel.PlayListViewModelFactory(requireContext())
        playListViewModel = ViewModelProvider(requireActivity(), viewModeFactory)[PlayListViewModel::class.java]
        sharedDataViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        initView()
        initClick()
    }

    private fun initView() {
        binding.apply {
            rvPlaylist.apply {
                adapter = playlistAdapter
                layoutManager = LinearLayoutManager(context)
            }
            cloudPlaylist.setBackgroundResource(R.drawable.shape_setting_item_bg)
            cloudPlaylist.typeface = resources.getFont(R.font.main_font_bold)
        }
        lifecycleScope.launch {
            val timeout = 3000L
            val startTime = System.currentTimeMillis()
            while (true) {
                if (playlistOnCloud.isNotEmpty() || System.currentTimeMillis() - startTime > timeout) {
                    playlistAdapter.submitList(playlistOnCloud)
                    _playlistSource.value = playlistOnCloud
                    binding.rvPlaylist.visibility = View.VISIBLE
                    binding.loading.visibility = View.GONE
                    break
                }
                delay(1000)
            }
        }
    }

    private fun initClick() {
        binding.apply {
            cloudPlaylist.setOnClickListener {
                cloudPlaylist.setBackgroundResource(R.drawable.shape_setting_item_bg)
                cloudPlaylist.typeface = resources.getFont(R.font.main_font_bold)
                devicePlaylist.setBackgroundResource(0)
                devicePlaylist.typeface = resources.getFont(R.font.main_font)
                type = MyApplication.CLOUD_TYPE
                playlistAdapter.submitList(playlistOnCloud)
            }
            devicePlaylist.setOnClickListener {
                cloudPlaylist.setBackgroundResource(0)
                cloudPlaylist.typeface = resources.getFont(R.font.main_font)
                devicePlaylist.setBackgroundResource(R.drawable.shape_setting_item_bg)
                devicePlaylist.typeface = resources.getFont(R.font.main_font_bold)
                type = MyApplication.DEVICE_TYPE
                playlistAdapter.submitList(playlistOnDevice)
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            playListViewModel.devicePlaylists.collectLatest { playlists ->
                playlistOnDevice = playlists.toMutableList()
                if (type == MyApplication.DEVICE_TYPE) {
                    playlistAdapter.submitList(playlists)
                }
            }
        }

        lifecycleScope.launch {
            playListViewModel.userPlaylist.collectLatest { playlists ->
                playlistOnCloud = playlists.toMutableList()
                if (type == MyApplication.CLOUD_TYPE) {
                    playlistAdapter.submitList(playlists)
                }
            }
        }
    }

}