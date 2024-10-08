package com.nghiatd.mixic.ui.home

import android.Manifest
import android.content.ComponentName
import android.content.Context.BIND_AUTO_CREATE
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.MainActivity
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
            val binder = service as MusicService.MusicBinder
            this@HomeFragment.service = binder.getMusicService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            service = null
            isBound = false
        }
    }

    private val isAtLeast13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val permissions = if (isAtLeast13) {
        arrayOf(
            Manifest.permission.READ_MEDIA_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                //TODO
            } else {
                showPermissionDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Intent(requireActivity(), MusicService::class.java).also { intent ->
            requireActivity().bindService(intent, serviceConnection, BIND_AUTO_CREATE)
            requireActivity().startService(intent)
        }
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        checkPermission()
    }

    private fun checkPermission() {
        if (permissions.any { checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED }) {
            requestPermissionsIfNeed()
        } else {
            //TODO
        }
    }

    private fun initView() {
        replaceFragment(HomeFirebaseFragment(), "Firebase")
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
                    minimizedSetViewOnCommand()
                }
                btnPrevious.setOnClickListener {
                    service?.playPrev()
                    minimizedSetViewOnCommand()
                }
                btnPlayPause.setOnClickListener {
                    val song = service?.currentPlaying?.value?.second
                    val imgRes =
                        if (service?.isPlayingFlow?.value == true) R.drawable.icon_play else R.drawable.icon_pause
                    Glide.with(btnPlayPause)
                        .load(imgRes)
                        .into(btnPlayPause)
                    service?.playPause(song)
                    minimizedSetViewOnCommand()
                }
            }
        }
    }

    private fun minimizedSetViewOnCommand() {
        lifecycleScope.launch {
            service?.currentPlaying?.collect { currentPlaying ->
                val song = currentPlaying?.second
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
        }
    }

    private fun openAppSettings() {
        val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
            addCategory(CATEGORY_DEFAULT)
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            addFlags(FLAG_ACTIVITY_NO_HISTORY)
            addFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        }

        startActivity(intent)
    }

    private fun requestPermissionsIfNeed() {
        if (permissions.all { checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }) {
            //TODO
        } else if (permissions.any { shouldShowRequestPermissionRationale(it) }) {
            showPermissionDialog()
        } else {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Notice!")
            setCancelable(false)
            setMessage("This app needs to access device storage to get music files. Please grant permission and restart the app!")
            setPositiveButton("Accept") { _, _ ->
                (activity as MainActivity).finish()
                openAppSettings()
            }
            setNegativeButton("Deny") { _, _ ->
                (activity as MainActivity).finish()
            }
        }.show()
    }

    private fun replaceFragment(fragment: Fragment, name: String?) {
        childFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(name)
            .commit()
    }

    private fun isFragmentInBackStack(fragmentManager: FragmentManager, fragmentTag: String): Boolean {
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