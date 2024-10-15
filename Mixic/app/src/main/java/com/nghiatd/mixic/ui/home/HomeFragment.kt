package com.nghiatd.mixic.ui.home

import android.content.ComponentName
import android.content.Context
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentHomeBinding
import com.nghiatd.mixic.receiver.BroadcastReceiver
import com.nghiatd.mixic.service.MusicService

class HomeFragment : Fragment(), BroadcastReceiver.SongListener {

    private lateinit var binding: FragmentHomeBinding

    private var service: MusicService? = null
    private var isBound = false
    private lateinit var broadcastReceiver: BroadcastReceiver
    private val isAtLeast13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            this@HomeFragment.service = binder.getMusicService()
            isBound = true
            replaceFragment(HomeFirebaseFragment(), "Firebase")
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
        broadcastReceiver = BroadcastReceiver(this)
        val intentFilter = IntentFilter("com.nghiatd.mixic.SONG_START")
        if (isAtLeast13) {
            requireActivity().registerReceiver(
                broadcastReceiver,
                intentFilter,
                Context.RECEIVER_EXPORTED
            )
        } else {
            requireActivity().registerReceiver(broadcastReceiver, intentFilter)
        }
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
                tvName.isSelected = true
                btnExpand.setOnClickListener {
                    minimizedLayout.root.visibility = View.GONE
                    bottomNav.visibility = View.GONE
                    replaceFragment(PlayingSongFragment(), "PlayingSong")
                }
                btnNext.setOnClickListener {
                    service?.playNext()
                }
                btnPrevious.setOnClickListener {
                    service?.playPrev()
                }
                btnPlayPause.setOnClickListener {
                    val song = service?.currentPlaying?.value?.second
                    val imgRes =
                        if (service?.isPlayingFlow?.value == true) R.drawable.icon_play else R.drawable.icon_pause
                    Glide.with(btnPlayPause)
                        .load(imgRes)
                        .into(btnPlayPause)
                    service?.playPause(song)
                }
            }
        }
    }

    private fun minimizedSetViewOnSongStart() {
        val song = service?.currentPlaying?.value?.second
        val playingSongFragment = isFragmentInBackStack(childFragmentManager, "PlayingSong")
        if (binding.minimizedLayout.root.visibility == View.GONE && !playingSongFragment) {
            binding.minimizedLayout.root.visibility = View.VISIBLE
        }
        binding.minimizedLayout.apply {
            tvName.text = song?.name
            tvArtist.text = song?.artist
            val uri = Uri.parse(song?.image)
            Glide.with(imgThumb)
                .load(uri)
                .apply(RequestOptions().transform(RoundedCorners(15)))
                .into(imgThumb)
        }

    }

    private fun replaceFragment(fragment: Fragment, name: String?) {
        if (isAdded) {
            childFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(name)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
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
        requireActivity().unregisterReceiver(broadcastReceiver)
    }

    fun getMusicService(): MusicService? {
        return this@HomeFragment.service
    }

    override fun onSongStart() {
        minimizedSetViewOnSongStart()
    }
}