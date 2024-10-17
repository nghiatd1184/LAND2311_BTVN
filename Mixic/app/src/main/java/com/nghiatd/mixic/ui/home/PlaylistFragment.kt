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
import com.nghiatd.mixic.adapter.PlaylistAdapter
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentPlaylistBinding
import com.nghiatd.mixic.data.viewmodel.PlayListViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PlaylistFragment : Fragment() {

    private lateinit var binding: FragmentPlaylistBinding
    private lateinit var playListViewModel: PlayListViewModel
    private lateinit var sharedDataViewModel: SharedDataViewModel
    private var playlistOnDevice: MutableList<Playlist> = mutableListOf()
    private val _playlistSource = MutableStateFlow<List<Playlist>>(emptyList())
    private val playlistSource = _playlistSource.asStateFlow()

    private val playlistAdapter: PlaylistAdapter by lazy {
        PlaylistAdapter(playListViewModel) { playlist ->
            sharedDataViewModel.setSection(Section(playlist.id, playlist.name, playlist.songs))
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
    }

    private fun initView() {
        binding.apply {
            rvPlaylist.apply {
                adapter = playlistAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            playListViewModel.devicePlaylists.collectLatest { playlists ->
//                playlistOnDevice = playlists.toMutableList()
//                _playlistSource.value = playlistOnDevice
                playlistAdapter.submitList(playlists)
            }
        }
    }

}