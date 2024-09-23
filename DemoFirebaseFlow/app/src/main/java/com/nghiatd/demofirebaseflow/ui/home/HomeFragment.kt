package com.nghiatd.demofirebaseflow.ui.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.demofirebaseflow.R
import com.nghiatd.demofirebaseflow.data.model.User
import com.nghiatd.demofirebaseflow.data.viewmodel.SharedViewModel
import com.nghiatd.demofirebaseflow.databinding.FragmentHomeBinding
import com.nghiatd.demofirebaseflow.ui.changepassword.ChangePasswordFragment
import com.nghiatd.demofirebaseflow.ui.login.LogInFragment


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            btnLogOut.setOnClickListener {
                val firebaseAuth = FirebaseAuth.getInstance()
                val email = firebaseAuth.currentUser?.email.toString()
                sharedPref.edit().putString("latest_email", email).apply()
                viewModel.setUser(User(email,""))
                firebaseAuth.signOut()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, LogInFragment())
                    .commit()
            }
            btnChangePassword.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, ChangePasswordFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

}