package com.nghiatd.rhythmtune.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.rhythmtune.R
import com.nghiatd.rhythmtune.auth.login
import com.nghiatd.rhythmtune.data.model.User
import com.nghiatd.rhythmtune.data.viewmodel.SharedViewModel
import com.nghiatd.rhythmtune.databinding.FragmentLoginBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
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

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            lifecycleScope.launch{
                viewModel.selectedUser.collectLatest { user ->
                    if (user != null) {
                        edtEmail.setText(user.email)
                        edtPassword.setText(user.password)
                        viewModel.setUser(null)
                    }
                }
            }
            tvSignUp.setOnClickListener {
//                replaceFragment(RegisterFragment())
            }
            tvForgotPassword.setOnClickListener {
//                replaceFragment(ForgotPasswordFragment())
            }
            btnSignIn.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.email_or_password_empty, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        login(email, password).collectLatest { task ->
                            if (task.isSuccessful) {
//                                parentFragmentManager.beginTransaction()
//                                    .replace(R.id.container, HomeFragment())
//                                    .commit()
                            } else {
                                Toast.makeText(requireContext(), task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
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