package com.nghiatd.demofirebaseflow.ui.changepassword

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.demofirebaseflow.R
import com.nghiatd.demofirebaseflow.auth.changePassword
import com.nghiatd.demofirebaseflow.data.viewmodel.SharedViewModel
import com.nghiatd.demofirebaseflow.databinding.FragmentChangePasswordBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class ChangePasswordFragment : Fragment() {

    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var viewModel: SharedViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.apply {
            btnChangePassword.setOnClickListener {
                val oldPassword = edtOldPassword.text.toString()
                val newPassword = edtNewPassword.text.toString()
                val confirmPassword = edtConfirmPassword.text.toString()
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), R.string.please_fill_all_fields, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), R.string.wrong_confirm_password, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        changePassword(oldPassword, newPassword).collectLatest { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), R.string.change_password_success, Toast.LENGTH_SHORT).show()
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