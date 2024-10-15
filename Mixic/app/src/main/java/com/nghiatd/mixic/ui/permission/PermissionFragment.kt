package com.nghiatd.mixic.ui.permission

import android.Manifest
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
import com.nghiatd.mixic.MainActivity
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentPermissionBinding
import com.nghiatd.mixic.ui.home.HomeFragment

class PermissionFragment : Fragment() {

    private lateinit var binding: FragmentPermissionBinding

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClick()
    }

    private fun initView() {
        if (isAtLeast13) {
            binding.tvExternalStorageTitle.setText(R.string.read_media_audio)
            binding.imgExternalStorage.setImageResource(R.drawable.icon_music)
        } else {
            binding.apply {
                tvExternalStorageTitle.setText(R.string.read_external_storage)
                imgExternalStorage.setImageResource(R.drawable.icon_file)
                tvPermissionSubtitle2.visibility = View.GONE
                tvNotificationTitle.visibility = View.GONE
                tvNotificationSubtitle.visibility = View.GONE
                imgNotification.visibility = View.GONE
            }
        }
    }

    private fun initClick() {
        binding.btnContinue.setOnClickListener {
            checkPermission()
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.all { it.value }) {
                replaceFragment()
            } else {
                showPermissionDialog()
            }
        }

    private fun checkPermission() {
        if (permissions.any {
                checkSelfPermission(
                    requireContext(),
                    it
                ) != PackageManager.PERMISSION_GRANTED
            }) {
            requestPermissionsIfNeed()
        } else {
            replaceFragment()
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
        if (permissions.all {
                checkSelfPermission(
                    requireContext(),
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            replaceFragment()
        } else if (permissions.any { shouldShowRequestPermissionRationale(it) }) {
            showPermissionDialog()
        } else {
            requestPermissionsLauncher.launch(permissions)
        }
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.notice))
            setCancelable(false)
            setMessage(getString(R.string.alert_on_denied_storage))
            setPositiveButton(getString(R.string.accept)) { _, _ ->
                (activity as MainActivity).finish()
                openAppSettings()
            }
            setNegativeButton(getString(R.string.deny)) { _, _ ->
                (activity as MainActivity).finish()
            }
        }.show()
    }

    private fun replaceFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .commit()
    }

}