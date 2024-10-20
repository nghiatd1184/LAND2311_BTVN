package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nghiatd.mixic.MyApplication
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.SongAdapter
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.viewmodel.SearchViewModel
import com.nghiatd.mixic.databinding.FragmentSearchBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: SearchViewModel
    private lateinit var searchType : String
    private var service: MusicService? = null
    private val adapter: SongAdapter by lazy {
        SongAdapter { song ->
            service?.setPlayList(listOf(song))
            service?.playPause(song)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        service = (parentFragment as HomeFragment).getMusicService()
        val viewModeFactory = SearchViewModel.SongViewModelFactory(requireContext())
        viewModel = ViewModelProvider(requireActivity(), viewModeFactory)[SearchViewModel::class.java]
        binding = FragmentSearchBinding.inflate(inflater, container, false)
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
            searchResult.apply {
                adapter = this@SearchFragment.adapter
                layoutManager = LinearLayoutManager(requireContext())
            }
            searchFromCloud.setBackgroundResource(R.drawable.shape_setting_item_bg)
            searchFromCloud.typeface = resources.getFont(R.font.main_font_bold)
            searchFromDevice.setBackgroundResource(0)
            searchFromDevice.typeface = resources.getFont(R.font.main_font)
            searchType = MyApplication.CLOUD_TYPE
        }
    }

    private fun initClick() {
        binding.apply {
            searchFromCloud.setOnClickListener {
                searchFromCloud.setBackgroundResource(R.drawable.shape_setting_item_bg)
                searchFromCloud.typeface = resources.getFont(R.font.main_font_bold)
                searchFromDevice.setBackgroundResource(0)
                searchFromDevice.typeface = resources.getFont(R.font.main_font)
                searchType = MyApplication.CLOUD_TYPE
                adapter.submitList(emptyList())
                searchBar.text.clear()
            }
            searchFromDevice.setOnClickListener {
                searchFromDevice.setBackgroundResource(R.drawable.shape_setting_item_bg)
                searchFromDevice.typeface = resources.getFont(R.font.main_font_bold)
                searchFromCloud.setBackgroundResource(0)
                searchFromCloud.typeface = resources.getFont(R.font.main_font)
                searchType = MyApplication.DEVICE_TYPE
                adapter.submitList(emptyList())
                searchBar.text.clear()
            }

            searchBar.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    val query = binding.searchBar.text.toString()
                    performSearch(query)
                    true
                } else {
                    false
                }
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewModel.resultFromDevice.collectLatest {
                adapter.submitList(it)
            }

        }

        lifecycleScope.launch {
            viewModel.resultFromFirebase.collectLatest {
                adapter.submitList(it)
            }
        }

        lifecycleScope.launch {
            service?.isPlayingFlow?.collectLatest { isPlaying ->
                adapter.isPlaying = isPlaying
                val imgPlayPause =
                    if (isPlaying) R.drawable.icon_pause else R.drawable.icon_play
                val minimizedPlayPauseBtn =
                    (parentFragment as HomeFragment).view?.findViewById<View>(R.id.minimized_layout)
                        ?.findViewById<ImageView>(R.id.btn_play_pause)
                Glide.with(minimizedPlayPauseBtn!!)
                    .load(imgPlayPause)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(minimizedPlayPauseBtn)
            }
        }

        lifecycleScope.launch {
            service?.currentPlaying?.collectLatest { currentPlaying ->
                adapter.playingSong = currentPlaying
            }
        }
    }

    private fun performSearch(query: String) {
        if (searchType == MyApplication.CLOUD_TYPE) {
            viewModel.searchSongFromFirebase(query.lowercase())
        } else {
            viewModel.searchSongFromDevice(query.lowercase())
        }
    }

}