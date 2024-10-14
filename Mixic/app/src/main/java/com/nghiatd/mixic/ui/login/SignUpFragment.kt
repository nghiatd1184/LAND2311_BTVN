package com.nghiatd.mixic.ui.login

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
import com.nghiatd.mixic.auth.registerUserByEmail
import com.nghiatd.mixic.data.model.User
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentSignUpBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: SharedDataViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initClick()
    }

    private fun initClick() {
        binding.apply {
            imgBack.setOnClickListener {
                viewModel.setUser(null)
                parentFragmentManager.popBackStack()
            }

            btnSignUp.setOnClickListener {
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword = edtConfirmPassword.text.toString()
                val name = edtName.text.toString()
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || name.isEmpty() || email.isBlank() || password.isBlank() || confirmPassword.isBlank() || name.isBlank()) {
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (password != confirmPassword) {
                    Toast.makeText(requireContext(), getString(R.string.wrong_confirm_password), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (password.length !in 6..16) {
                    Toast.makeText(requireContext(), getString(R.string.password_length_error), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        registerUserByEmail(email, password, name).collectLatest{ task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), "${getString(R.string.register_success)} $email", Toast.LENGTH_SHORT).show()
                                val user = User(email = email)
                                Log.d("NGHIA", "register: $user")
                                viewModel.setUser(user)
                                parentFragmentManager.popBackStack()
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