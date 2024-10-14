package com.nghiatd.mixic.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.mixic.R
import com.nghiatd.mixic.auth.login
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentLoginBinding
import com.nghiatd.mixic.ui.home.HomeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: SharedDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        initViews()
        initClicks()
    }

    private fun initViews() {
        val sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val latestEmail = sharedPref.getString("latest_login_email", "")
        binding.edtEmail.setText(latestEmail)
        binding.edtPassword.requestFocus()
    }

    private fun initClicks() {
        binding.apply {
            btnLogin.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.email_or_password_empty.toString(), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        login(email, password).collectLatest { task ->
                            if (task.isSuccessful) {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.main_container, HomeFragment())
                                    .commit()
                            } else {
                                Toast.makeText(requireContext(), task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            }
            tvSignUp.setOnClickListener {
                replaceFragment(SignUpFragment())
            }
            tvForgotPassword.setOnClickListener {
                replaceFragment(ForgotPasswordFragment())
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            viewModel.selectedUser.collectLatest { user ->
                Log.d("NGHIA", "listenViewModel: $user")
                if (user != null) {
                    binding.edtEmail.setText(user.email)
                    binding.edtPassword.requestFocus()
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}