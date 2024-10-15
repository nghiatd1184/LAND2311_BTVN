package com.nghiatd.mixic.ui.login

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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.mixic.MainActivity
import com.nghiatd.mixic.R
import com.nghiatd.mixic.auth.updateProfile
import com.nghiatd.mixic.auth.uploadPhotoToFirebaseStorage
import com.nghiatd.mixic.databinding.FragmentUpdateProfileBinding
import com.nghiatd.mixic.ui.home.HomeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UpdateProfileFragment : Fragment() {

    private lateinit var binding: FragmentUpdateProfileBinding
    private var avatarUri: Uri? = null
    private val permission = android.Manifest.permission.READ_MEDIA_IMAGES

    private val requestImagePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                getPhotoFromGallery()
            } else {
                showPermissionDialog()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUpdateProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        binding.apply {
            btnChooseAvatar.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (requireContext().checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                        requestImagePermissionLauncher.launch(permission)
                        Log.d("NGHIA", "Request permission")
                    } else {
                        Log.d("NGHIA", "Permission granted")
                        getPhotoFromGallery()
                    }
                } else {
                    getPhotoFromGallery()
                }
            }

            btnUpdateProfile.setOnClickListener {
                binding.loading.visibility = View.VISIBLE
                binding.btnLayout.visibility = View.GONE
                val name = edtDisplayName.text.toString()
                if (name.isEmpty() || name.isBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (name.length !in 5..12) {
                    Toast.makeText(requireContext(), getString(R.string.display_name_length_error), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                lifecycleScope.launch {
                    if (avatarUri != null) {
                        val userUid = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        val photoUrl = uploadPhotoToFirebaseStorage(avatarUri!!, userUid)
                        if (photoUrl != null) {
                            updateProfile(name, Uri.parse(photoUrl)).collectLatest { task ->
                                if (task.isSuccessful) {
                                    replaceFragment()
                                } else {
                                    Toast.makeText(requireContext(), getString(R.string.update_profile_error), Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(requireContext(), getString(R.string.upload_photo_error), Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        updateProfile(name, null).collectLatest { task ->
                            if (task.isSuccessful) {
                                replaceFragment()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.update_profile_error),
                                    Toast.LENGTH_SHORT
                                ).show()

                            }
                        }
                    }
                }
                binding.loading.visibility = View.GONE
                binding.btnLayout.visibility = View.VISIBLE
            }

            btnSkip.setOnClickListener {
                replaceFragment()
            }
        }
    }

    private val getPhotoFromGalleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            avatarUri = result.data?.data
            Glide.with(binding.imgAvatar)
                .load(avatarUri)
                .apply(RequestOptions().transform(RoundedCorners(15)))
                .into(binding.imgAvatar)
        }
    }

    private fun getPhotoFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        getPhotoFromGalleryLauncher.launch(intent)
    }

    private fun replaceFragment() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, HomeFragment())
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }

    private fun showPermissionDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle(getString(R.string.notice))
            setCancelable(false)
            setMessage(getString(R.string.alert_on_denied_image))
            setPositiveButton(R.string.accept) { _, _ ->
                (activity as MainActivity).finish()
                openAppSettings()
            }
            setNegativeButton(R.string.deny) { _, _ ->
                (activity as MainActivity).finish()
            }
        }.show()
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

}