package com.nghiatd.mixic.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.nghiatd.mixic.R
import com.nghiatd.mixic.auth.changePassword
import com.nghiatd.mixic.databinding.FragmentChangePasswordBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChangePasswordFragment : Fragment() {
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
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

            btnChangePassword.setOnClickListener {
                val oldPassword = edtCurrentPassword.text.toString()
                val newPassword = edtNewPassword.text.toString()
                val confirmPassword = edtConfirmNewPassword.text.toString()
                if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(requireContext(), getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else if (newPassword != confirmPassword) {
                    Toast.makeText(requireContext(), getString(R.string.wrong_confirm_password), Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        changePassword(oldPassword, newPassword).collectLatest { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(requireContext(), getString(R.string.change_password_success), Toast.LENGTH_SHORT).show()
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