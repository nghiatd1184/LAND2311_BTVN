package com.nghiatd.demofirebaseflow.ui.register

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
import com.nghiatd.demofirebaseflow.auth.registerUserByEmail
import com.nghiatd.demofirebaseflow.data.model.User
import com.nghiatd.demofirebaseflow.data.viewmodel.SharedViewModel
import com.nghiatd.demofirebaseflow.databinding.FragmentRegisterBinding
import com.nghiatd.demofirebaseflow.ui.login.LogInFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.apply {
            tvLogin.setOnClickListener {
                lifecycleScope.launch {
                    viewModel.setUser(null)
                }
                parentFragmentManager.popBackStack()
            }
            btnRegister.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword = edtConfirmPassword.text.toString()
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (password != confirmPassword) {
                    Toast.makeText(requireContext(), R.string.wrong_confirm_password, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        registerUserByEmail(email, password).collectLatest{ task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "${R.string.register_success} $email", Toast.LENGTH_SHORT).show()
                                val user = User(email, password)
                                viewModel.setUser(user)
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.container, LogInFragment())
                                    .commit()
                            } else {
                                Toast.makeText(requireContext(), task.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

    }


}