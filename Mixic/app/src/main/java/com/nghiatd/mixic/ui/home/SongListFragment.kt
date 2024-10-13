package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.util.Log
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
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentSongListBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SongListFragment : Fragment() {

    private lateinit var binding: FragmentSongListBinding
    private lateinit var sharedViewModel: SharedDataViewModel
    private lateinit var servicePlayList: MutableList<Song>
    private var feature: Feature? = null
    private var category: Category? = null
    private var section: Section? = null
    private var service: MusicService? = null

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
        service = (parentFragment as HomeFragment).getMusicService()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        binding = FragmentSongListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        initView()
        initClick()
    }

    private fun initView() {
        if (feature != null) {
            binding.tvName.text = feature?.name
            binding.imgFeature.visibility = View.VISIBLE
            Glide.with(binding.imgFeature)
                .load(feature?.image)
                .into(binding.imgFeature)
        } else if (category != null) {
            binding.tvName.text = category?.name
            setImgForNonFeature(category?.songs!!)
        } else {
            binding.tvName.text = section?.name
            setImgForNonFeature(section?.songs!!)
        }
        recyclerViewInit()
    }

    private fun setImgForNonFeature(songs: List<Song>) {
        binding.imgForNonFeature.visibility = View.VISIBLE
        Glide.with(binding.img1)
            .load(songs[0].image)
            .into(binding.img1)
        Glide.with(binding.img2)
            .load(songs[1].image)
            .into(binding.img2)
        Glide.with(binding.img3)
            .load(songs[2].image)
            .into(binding.img3)
        Glide.with(binding.img4)
            .load(songs[3].image)
            .into(binding.img4)
    }

    private fun initClick() {
        binding.imgBack.setOnClickListener {
            sharedViewModel.setCategory(null)
            sharedViewModel.setFeature(null)
            sharedViewModel.setSection(null)
            binding.imgForNonFeature.visibility = View.GONE
            binding.imgFeature.visibility = View.GONE
            parentFragmentManager.popBackStack()
        }
    }

    private fun recyclerViewInit() {
        if (feature != null) {
            songAdapter.submitList(feature?.songs)
        } else if (category != null) {
            songAdapter.submitList(category?.songs)
        } else {
            songAdapter.submitList(section?.songs)
        }
        binding.recyclerView.apply {
            adapter = songAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            sharedViewModel.selectedFeature.collectLatest {
                this@SongListFragment.feature = it
            }
        }

        lifecycleScope.launch {
            sharedViewModel.selectedCategory.collectLatest {
                this@SongListFragment.category = it
            }
        }

        lifecycleScope.launch {
            sharedViewModel.selectedSection.collectLatest {
                this@SongListFragment.section = it
            }
        }

        lifecycleScope.launch {
            service?.allSongs?.collectLatest {
                this@SongListFragment.servicePlayList = it.toMutableList()
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
                songAdapter.playingSong = currentPlaying?.second
            }
        }
    }
}