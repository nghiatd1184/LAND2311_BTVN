package com.nghiatd.mixic.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.mixic.MainActivity
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentProfileBinding
import com.nghiatd.mixic.ui.login.LoginFragment

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        binding.apply {
            tvSignOut.setOnClickListener {
                val firebaseAuth = FirebaseAuth.getInstance()
                val email = firebaseAuth.currentUser?.email.toString()
                val sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
                sharedPref.edit().putString("latest_login_email", email).apply()
                firebaseAuth.signOut()
                (activity as MainActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.main_container, LoginFragment())
                    .commit()
            }
        }
    }

}