package com.nghiatd.mixic.ui.home

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.CategoryAdapter
import com.nghiatd.mixic.adapter.FeatureAdapter
import com.nghiatd.mixic.adapter.SectionAdapter
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.viewmodel.HomeFirebaseViewModel
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentHomeFirebaseBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs

class HomeFirebaseFragment : Fragment() {

    private lateinit var binding: FragmentHomeFirebaseBinding
    private lateinit var firebaseViewModel: HomeFirebaseViewModel
    private lateinit var sharedViewModel: SharedDataViewModel
    private val sectionList = mutableListOf<Section>()
    private lateinit var newSongs: Section
    private lateinit var servicePlayList: MutableList<Song>
    private var service: MusicService? = null

    private val featureAdapter: FeatureAdapter by lazy {
        FeatureAdapter { feature ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, SongListFragment())
                .addToBackStack(null)
                .commit()
            sharedViewModel.setFeature(feature)
        }
    }

    private val categoryAdapter: CategoryAdapter by lazy {
        CategoryAdapter { category ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, SongListFragment())
                .addToBackStack(null)
                .commit()
            sharedViewModel.setCategory(category)
        }
    }

    private val section1Adapter: SectionAdapter by lazy {
        SectionAdapter { song ->
            if (servicePlayList != sectionList[0].songs) {
                service?.setPlayList(sectionList[0].songs)
            }
            service?.playPause(song)
        }
    }

    private val section2Adapter: SectionAdapter by lazy {
        SectionAdapter { song ->
            if (servicePlayList != sectionList[1].songs) {
                service?.setPlayList(sectionList[1].songs)
            }
            service?.playPause(song)
        }
    }

    private val newSongsAdapter: SectionAdapter by lazy {
        SectionAdapter { song ->
            if (servicePlayList != newSongs.songs) {
                service?.setPlayList(newSongs.songs)
            }
            service?.playPause(song)
        }
    }

    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = featureAdapter.itemCount
            if (itemCount > 0) {
                val nextItem = (binding.viewPagerFeature.currentItem + 1) % itemCount
                binding.viewPagerFeature.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // Change slide every 3 seconds
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        service = (parentFragment as HomeFragment).getMusicService()
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        firebaseViewModel = ViewModelProvider(requireActivity())[HomeFirebaseViewModel::class.java]
        binding = FragmentHomeFirebaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        initView()
    }

    private fun initView() {
        initViewFeature()
        initViewCategory()
        handler.postDelayed(autoScrollRunnable, 3000)
    }

    private fun initViewFeature() {
        binding.viewPagerFeature.apply {
            adapter = featureAdapter
            offscreenPageLimit = 1
            setPageTransformer { page, position ->
                page.translationX = -page.translationX * position
                page.scaleY = 1 - (0.15f * abs(position))
                binding.viewPagerFeature.setBackgroundColor(Color.TRANSPARENT)
            }
        }

    }

    private fun initViewCategory() {
        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            firebaseViewModel.allSection.collectLatest {
                sectionList.clear()
                sectionList.addAll(it)
                if (sectionList.size == 2) {
                    setupSection(
                        sectionList[0],
                        binding.section1Title,
                        binding.section1Rv,
                        binding.section1ViewAll,
                        section1Adapter
                    )
                    setupSection(
                        sectionList[1],
                        binding.section2Title,
                        binding.section2Rv,
                        binding.section2ViewAll,
                        section2Adapter
                    )
                }
            }
        }

        lifecycleScope.launch {
            firebaseViewModel.newSongs.collectLatest {
                if (it.isNotEmpty()) {
                    newSongs = Section("section_new_songs", "New Songs", it)
                    withContext(Dispatchers.Main) {
                        setupSection(
                            newSongs,
                            binding.newSongsTitle,
                            binding.newSongsRv,
                            binding.newSongsViewAll,
                            newSongsAdapter
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            firebaseViewModel.allCategory.collectLatest {
                categoryAdapter.submitList(it)
                categoryAdapter.notifyDataSetChanged()

            }
        }

        lifecycleScope.launch {
            firebaseViewModel.allFeature.collectLatest {
                featureAdapter.submitList(it)
                featureAdapter.notifyDataSetChanged()
            }
        }

        lifecycleScope.launch {
            service?.allSongs?.collectLatest {
                this@HomeFirebaseFragment.servicePlayList = it.toMutableList()
            }
        }

        lifecycleScope.launch {
            service?.isPlayingFlow?.collectLatest { isPlaying ->
                section1Adapter.isPlaying = isPlaying
                section2Adapter.isPlaying = isPlaying
                newSongsAdapter.isPlaying = isPlaying
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
                section1Adapter.playingSong = currentPlaying
                section2Adapter.playingSong = currentPlaying
                newSongsAdapter.playingSong = currentPlaying
            }
        }
    }

    private fun setupSection(
        section: Section,
        title: TextView,
        recyclerView: RecyclerView,
        viewAll: TextView,
        sectionAdapter: SectionAdapter
    ) {
        title.text = section.name
        viewAll.setOnClickListener {
            sharedViewModel.setSection(section)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, SongListFragment())
                .addToBackStack(null)
                .commit()
        }
        viewAll.visibility = View.VISIBLE
        recyclerView.apply {
            adapter = sectionAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        sectionAdapter.submitList(section.songs.take(5))
        sectionAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
    }
}