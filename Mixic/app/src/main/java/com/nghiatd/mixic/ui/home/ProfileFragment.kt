package com.nghiatd.mixic.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.mixic.MainActivity
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentProfileBinding
import com.nghiatd.mixic.service.MusicService
import com.nghiatd.mixic.ui.login.ChangePasswordFragment
import com.nghiatd.mixic.ui.login.LoginFragment
import com.nghiatd.mixic.ui.login.UpdateProfileFragment

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private var service: MusicService? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        service = (parentFragment as HomeFragment).getMusicService()
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
        initView()
    }

    private fun initView() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val name = firebaseAuth.currentUser?.displayName
        val photoUrl = firebaseAuth.currentUser?.photoUrl
        binding.apply {
            profileName.text = name
            photoUrl?.let {
                Glide.with(profileImage)
                    .load(it)
                    .into(profileImage)
            } ?: Glide.with(profileImage)
                .load(R.drawable.default_avatar)
                .into(profileImage)
        }
    }

    private fun initClick() {
        binding.apply {
            tvSignOut.setOnClickListener {
                val firebaseAuth = FirebaseAuth.getInstance()
                val email = firebaseAuth.currentUser?.email.toString()
                val sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
                sharedPref.edit().putString("latest_login_email", email).apply()
                firebaseAuth.signOut()
                service?.isPlayingFlow?.value = false
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, LoginFragment())
                    .commit()
            }
            tvEditProfile.setOnClickListener {
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, UpdateProfileFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .commit()
            }

            tvChangePassword.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, ChangePasswordFragment())
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    .addToBackStack(null)
                    .commit()
            }

            tvChangeTheme.setOnClickListener {
                val popupMenu = PopupMenu(requireContext(), it)
                popupMenu.menuInflater.inflate(R.menu.menu_theme, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_dark -> setTheme(AppCompatDelegate.MODE_NIGHT_YES)
                        R.id.action_light -> setTheme(AppCompatDelegate.MODE_NIGHT_NO)
                        R.id.action_system -> setTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    }
                    true
                }
                popupMenu.show()
            }

        }
    }

    private fun setTheme(mode: Int) {
        val sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        sharedPref.edit().putInt("theme", mode).apply()
        AppCompatDelegate.setDefaultNightMode(mode)
        service?.isPlayingFlow?.value = false
        requireActivity().recreate()
    }

}