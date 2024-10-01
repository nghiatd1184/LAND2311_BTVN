package com.nghiatd.rhythmtune.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.rhythmtune.R
import com.nghiatd.rhythmtune.auth.registerUserByEmail
import com.nghiatd.rhythmtune.data.model.User
import com.nghiatd.rhythmtune.data.viewmodel.SharedViewModel
import com.nghiatd.rhythmtune.databinding.FragmentRegisterBinding
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
            tvBack.setOnClickListener {
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