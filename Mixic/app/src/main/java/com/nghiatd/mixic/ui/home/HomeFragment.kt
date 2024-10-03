package com.nghiatd.mixic.ui.home

import android.Manifest
import android.content.ContextWrapper
import android.content.Intent
import android.content.Intent.CATEGORY_DEFAULT
import android.content.Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.Intent.FLAG_ACTIVITY_NO_HISTORY
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.nghiatd.mixic.MainActivity
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.viewmodel.SongViewModel
import com.nghiatd.mixic.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding



    private val isAtLeast13 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    private val permission = if (isAtLeast13) Manifest.permission.READ_MEDIA_AUDIO
    else Manifest.permission.READ_EXTERNAL_STORAGE

    private val requestReadExternalPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
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
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        checkPermission()
    }

    private fun checkPermission() {
        if (checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
            requestReadExternalIfNeed()
        } else {
            //TODO
        }
    }

    private fun initView() {
        replaceFragment(HomeFirebaseFragment())
        binding.apply {
            bottomNav.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.home -> {
                        replaceFragment(HomeFirebaseFragment())
                        true
                    }
                    R.id.device -> {
                        replaceFragment(DeviceFragment())
                        true
                    }
                    R.id.search -> {
                        replaceFragment(SearchFragment())
                        true
                    }
                    else -> {
                        replaceFragment(ProfileFragment())
                        true
                    }
                }
            }
//            minimizedLayout.root.setOnClickListener {
//                minimizedLayout.root.visibility = View.GONE
//                fragmentPlayingSong.root.visibility = View.VISIBLE
//                bottomNav.visibility = View.GONE
//            }
            fragmentPlayingSong.imgDownCollapse.setOnClickListener {
                minimizedLayout.root.visibility = View.VISIBLE
                fragmentPlayingSong.root.visibility = View.GONE
                bottomNav.visibility = View.VISIBLE
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

    private fun requestReadExternalIfNeed() {
        when {
            checkSelfPermission(requireContext(),permission)
                    == PackageManager.PERMISSION_GRANTED -> {
                //TODO
            }

            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionDialog()
            }

            else -> {
                requestReadExternalPermissionLauncher.launch(
                    permission
                )
            }
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

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.container.id, fragment)
            .addToBackStack(null)
            .commit()
    }
}