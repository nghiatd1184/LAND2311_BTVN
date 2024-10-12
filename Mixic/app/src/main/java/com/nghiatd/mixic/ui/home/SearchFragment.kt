package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.viewmodel.FirebaseDataViewModel
import com.nghiatd.mixic.data.viewmodel.DeviceSongViewModel
import com.nghiatd.mixic.databinding.FragmentSearchBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var firebaseViewModel: FirebaseDataViewModel
    private lateinit var deviceViewModel: DeviceSongViewModel
    private lateinit var songFromCloud: MutableList<Song>
    private lateinit var songFromDevice: MutableList<Song>
    private var service : MusicService? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        service = (parentFragment as HomeFragment).getMusicService()
        firebaseViewModel = ViewModelProvider(this)[FirebaseDataViewModel::class.java]
        deviceViewModel = ViewModelProvider(this, DeviceSongViewModel.SongViewModelFactory(requireContext()))[DeviceSongViewModel::class.java]
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            firebaseViewModel.allSong.collectLatest {
                songFromCloud = it.toMutableList()
            }

        }
        lifecycleScope.launch {
            deviceViewModel.allSongs.collectLatest {
                songFromDevice = it.toMutableList()
            }
        }
    }
}