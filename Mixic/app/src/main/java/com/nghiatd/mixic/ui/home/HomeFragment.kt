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
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.databinding.FragmentHomeBinding
import com.nghiatd.mixic.service.MusicService
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private var service: MusicService? = null
    private var isBound = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            if (!isAdded) return
            val binder = service as MusicService.MusicBinder
            this@HomeFragment.service = binder.getMusicService()
            isBound = true
            requireActivity().startForegroundService(
                Intent(
                    requireActivity(),
                    MusicService::class.java
                )
            )
            replaceFragment(HomeFirebaseFragment(), "Firebase")
            minimizedSetViewOnSongStart()
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val intent = Intent(requireActivity(), MusicService::class.java)
        requireActivity().bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        binding.apply {
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        if (isFragmentInBackStack(childFragmentManager, "Firebase")) {
                            childFragmentManager.popBackStack("Firebase", 0)
                        } else {
                            replaceFragment(HomeFirebaseFragment(), "Firebase")
                        }
                        true
                    }

                    R.id.device -> {
                        if (isFragmentInBackStack(childFragmentManager, "Device")) {
                            childFragmentManager.popBackStack("Device", 0)
                        } else {
                            replaceFragment(DeviceFragment(), "Device")
                        }
                        true
                    }

                    R.id.playlists -> {
                        if (isFragmentInBackStack(childFragmentManager, "Playlist")) {
                            childFragmentManager.popBackStack("Playlist", 0)
                        } else {
                            replaceFragment(PlaylistFragment(), "Playlist")
                        }
                        true
                    }

                    R.id.search -> {
                        if (isFragmentInBackStack(childFragmentManager, "Search")) {
                            childFragmentManager.popBackStack("Search", 0)
                        } else {
                            replaceFragment(SearchFragment(), "Search")
                        }
                        true
                    }

                    else -> {
                        if (isFragmentInBackStack(childFragmentManager, "Profile")) {
                            childFragmentManager.popBackStack("Profile", 0)
                        } else {
                            replaceFragment(ProfileFragment(), "Profile")
                        }
                        true
                    }
                }
            }

            minimizedLayout.apply {
                btnExpand.setOnClickListener {
                    minimizedLayout.root.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                    val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
                    val animationSet = AnimationSet(true).apply {
                        addAnimation(slideDownAnimation)
                    }
                    minimizedLayout.root.startAnimation(animationSet)
                    bottomNav.startAnimation(animationSet)
                    replaceFragment(PlayingSongFragment(), "PlayingSong")
                }
                btnNext.setOnClickListener {
                    service?.playNext()
                }
                btnPrevious.setOnClickListener {
                    service?.playPrev()
                }
                btnPlayPause.setOnClickListener {
                    val song = service?.currentPlaying?.value
                    val imgRes =
                        if (service?.isPlayingFlow?.value == true) R.drawable.icon_play else R.drawable.icon_pause
                    Glide.with(btnPlayPause)
                        .load(imgRes)
                        .transition(DrawableTransitionOptions.withCrossFade(500))
                        .into(btnPlayPause)
                    service?.playPause(song)
                }
                root.setOnClickListener {
                    minimizedLayout.root.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                    val slideDownAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_down)
                    val animationSet = AnimationSet(true).apply {
                        addAnimation(slideDownAnimation)
                    }
                    minimizedLayout.root.startAnimation(animationSet)
                    bottomNav.startAnimation(animationSet)
                    replaceFragment(PlayingSongFragment(), "PlayingSong")
                }
            }
        }
    }

    private fun minimizedSetViewOnSongStart() {
        lifecycleScope.launch {
            service?.currentPlaying?.collectLatest { currentPlaying ->
                val song: Song? = currentPlaying
                song?.let {
                    val playingSongFragment =
                        isFragmentInBackStack(childFragmentManager, "PlayingSong")
                    if (binding.minimizedLayout.root.visibility == View.GONE && !playingSongFragment) {
                        binding.minimizedLayout.root.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.slide_up))
                        binding.minimizedLayout.root.visibility = View.VISIBLE
                    }
                    binding.minimizedLayout.apply {
                        tvName.text = song.name
                        tvName.isSelected = true
                        tvArtist.text = song.artist
                        val uri = Uri.parse(song.image)
                        Glide.with(imgThumb)
                            .load(uri)
                            .transition(DrawableTransitionOptions.withCrossFade(500))
                            .error(R.drawable.splash_img)
                            .into(imgThumb)
                    }
                }

            }
        }
    }

    private fun replaceFragment(fragment: Fragment, name: String?) {
        if (isAdded) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(name)
                .commit()
        }
    }

    private fun isFragmentInBackStack(
        fragmentManager: FragmentManager,
        fragmentTag: String
    ): Boolean {
        val backStackEntryCount = fragmentManager.backStackEntryCount

        for (i in 0 until backStackEntryCount) {
            val backStackEntry = fragmentManager.getBackStackEntryAt(i)
            if (backStackEntry.name == fragmentTag) {
                return true
            }
        }
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (isBound) {
            requireContext().unbindService(serviceConnection)
            isBound = false
        }
    }

    fun getMusicService(): MusicService? {
        return this@HomeFragment.service
    }

}