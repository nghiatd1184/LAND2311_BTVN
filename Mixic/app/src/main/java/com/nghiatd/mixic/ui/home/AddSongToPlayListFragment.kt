package com.nghiatd.mixic.ui.home

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.PlaylistAdapter
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.viewmodel.PlayListViewModel
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentAddSongToPlaylistBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddSongToPlayListFragment : Fragment() {

    private lateinit var binding: FragmentAddSongToPlaylistBinding
    private lateinit var playListViewModel: PlayListViewModel
    private lateinit var sharedDataViewModel: SharedDataViewModel
    private var song: Song? = null
    private var playlistOnDevice: MutableList<Playlist> = mutableListOf()
    private val _playlistSource = MutableStateFlow<List<Playlist>>(emptyList())
    private val playlistSource = _playlistSource.asStateFlow()

    private val playlistAdapter: PlaylistAdapter by lazy {
        PlaylistAdapter(playListViewModel) { playlist ->

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
        binding = FragmentAddSongToPlaylistBinding.inflate(inflater, container, false)
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
            recyclerView.apply {
                adapter = playlistAdapter
                layoutManager = LinearLayoutManager(context)
            }
        }
    }

    private fun initClick() {
        binding.apply {
            tvNewPlaylist.setOnClickListener {
                if (song != null) {
                    showDialogOnCreatePlaylist(song!!)
                }
            }

            imgBack.setOnClickListener {
                sharedDataViewModel.setSong(null)
                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            playListViewModel.devicePlaylists.collectLatest {
                playlistOnDevice.clear()
                playlistOnDevice.addAll(it)
                playlistAdapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            sharedDataViewModel.selectedSong.collectLatest {
                Log.d("NGHIA", "listenViewModel it: $it ${sharedDataViewModel.selectedSong.value}")
                this@AddSongToPlayListFragment.song = it
                Log.d("NGHIA", "listenViewModel: $song")
            }
        }
    }

    private fun showDialogOnCreatePlaylist(song: Song) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.custom_add_song_to_playlist_dialog)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val tvTitle = dialog.findViewById<TextView>(R.id.tv_title)
        val etPlaylistName = dialog.findViewById<TextView>(R.id.et_playlist_name)
        val tvCancel = dialog.findViewById<TextView>(R.id.tv_negative)
        val tvCreate = dialog.findViewById<TextView>(R.id.tv_positive)

        val titleText = StringBuilder()
        titleText.append("Add ${song.name} to new playlist")
        tvTitle.text = titleText.toString()
        val playlistName = StringBuilder()
        playlistName.append("New Playlist ${playlistOnDevice.size + 1}")
        etPlaylistName.text = playlistName.toString()
        etPlaylistName.requestFocus()
        tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        tvCreate.setOnClickListener {
            val name = etPlaylistName.text.toString()
            if (name.isEmpty() || name.isBlank()){
                Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else{
                playListViewModel.addSongToPlaylist(null, name, song)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

}