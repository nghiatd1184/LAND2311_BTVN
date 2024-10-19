package com.nghiatd.mixic.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nghiatd.mixic.R
import com.nghiatd.mixic.auth.forgotPassword
import com.nghiatd.mixic.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
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
            btnFindPassword.setOnClickListener {
                binding.loading.visibility = View.VISIBLE
                binding.btnFindPassword.visibility = View.GONE
                val email = edtEmail.text.toString()
                if (email.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.please_fill_email),
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.loading.visibility = View.GONE
                    binding.btnFindPassword.visibility = View.VISIBLE
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        forgotPassword(email).collectLatest { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.reset_password_success),
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.popBackStack()
                                binding.loading.visibility = View.GONE
                                binding.btnFindPassword.visibility = View.VISIBLE
                            } else {

                                Toast.makeText(
                                    requireContext(),
                                    task.exception?.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.loading.visibility = View.GONE
                                binding.btnFindPassword.visibility = View.VISIBLE
                            }
                        }
                    }
                }

            }
        }
    }
}