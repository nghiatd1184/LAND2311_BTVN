package com.nghiatd.demofirebaseflow.ui.forgotpassword

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
import com.nghiatd.demofirebaseflow.auth.forgotPassword
import com.nghiatd.demofirebaseflow.data.model.User
import com.nghiatd.demofirebaseflow.data.viewmodel.SharedViewModel
import com.nghiatd.demofirebaseflow.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
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
            btnFindPassword.setOnClickListener {
                val email = edtEmail.text.toString()
                if (email.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.please_fill_email, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        forgotPassword(email).collectLatest { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    R.string.reset_password_success,
                                    Toast.LENGTH_SHORT
                                ).show()
                                val user = User(email, "")
                                viewModel.setUser(user)
                                parentFragmentManager.popBackStack()
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

}