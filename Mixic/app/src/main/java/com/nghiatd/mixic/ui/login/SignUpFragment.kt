package com.nghiatd.mixic.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.nghiatd.mixic.R
import com.nghiatd.mixic.auth.registerUserByEmail
import com.nghiatd.mixic.databinding.FragmentSignUpBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                parentFragmentManager.popBackStack()
            }

            btnSignUp.setOnClickListener {
                binding.loading.visibility = View.VISIBLE
                binding.btnSignUp.visibility = View.GONE
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword = edtConfirmPassword.text.toString()
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_fill_all_fields),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loading.visibility = View.GONE
                    binding.btnSignUp.visibility = View.VISIBLE
                    return@setOnClickListener
                } else if (password != confirmPassword) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.wrong_confirm_password),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loading.visibility = View.GONE
                    binding.btnSignUp.visibility = View.VISIBLE
                    return@setOnClickListener
                } else if (password.length !in 6..16) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.password_length_error),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loading.visibility = View.GONE
                    binding.btnSignUp.visibility = View.VISIBLE
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        registerUserByEmail(email, password).collectLatest { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "${getString(R.string.register_success)} $email",
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.main_container, UpdateProfileFragment())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()

                                binding.loading.visibility = View.GONE
                                binding.btnSignUp.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    task.exception?.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.loading.visibility = View.GONE
                                binding.btnSignUp.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }
}