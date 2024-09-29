package com.nghiatd.demofirebaseflow.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.demofirebaseflow.R
import com.nghiatd.demofirebaseflow.auth.login
import com.nghiatd.demofirebaseflow.data.model.User
import com.nghiatd.demofirebaseflow.data.viewmodel.SharedViewModel
import com.nghiatd.demofirebaseflow.ui.register.RegisterFragment
import com.nghiatd.demofirebaseflow.databinding.FragmentLogInBinding
import com.nghiatd.demofirebaseflow.ui.forgotpassword.ForgotPasswordFragment
import com.nghiatd.demofirebaseflow.ui.home.HomeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val latestEmail = sharedPref.getString("latest_email", "")
        lifecycleScope.launch {
            viewModel.setUser(User(latestEmail.toString(), ""))
        }

        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            lifecycleScope.launch {
                viewModel.selectedUser.collectLatest { user ->
                    if (user != null) {
                        edtEmail.setText(user.email)
                        edtPassword.setText(user.password)
                        if (user.password.isEmpty()) {
                            edtPassword.requestFocus()
                        }
                        viewModel.setUser(null)
                    }
                }
            }
            tvSignUp.setOnClickListener {
                replaceFragment(RegisterFragment())
            }
            tvForgotPassword.setOnClickListener {
                replaceFragment(ForgotPasswordFragment())
            }
            btnSignIn.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        R.string.email_or_password_empty,
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        login(email, password).collectLatest { task ->
                            if (task.isSuccessful) {
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.container, HomeFragment())
                                    .commit()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    task.exception?.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                    }
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }


}