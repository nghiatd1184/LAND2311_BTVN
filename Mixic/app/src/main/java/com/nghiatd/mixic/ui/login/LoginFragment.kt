package com.nghiatd.mixic.ui.login

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.mixic.R
import com.nghiatd.mixic.auth.login
import com.nghiatd.mixic.databinding.FragmentLoginBinding
import com.nghiatd.mixic.ui.home.HomeFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClicks()
    }

    private fun initView() {
        val sharedPref = requireContext().getSharedPreferences("data", Context.MODE_PRIVATE)
        val email = sharedPref.getString("latest_login_email", "")
        binding.edtEmail.setText(email)
        binding.edtPassword.requestFocus()
    }

    private fun initClicks() {
        binding.apply {
            btnLogin.setOnClickListener {
                binding.loading.visibility = View.VISIBLE
                binding.btnLogin.visibility = View.GONE
                val email = edtEmail.text.toString()
                val password = edtPassword.text.toString()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.email_or_password_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    lifecycleScope.launch {
                        login(email, password).collectLatest { task ->
                            val displayName = FirebaseAuth.getInstance().currentUser?.displayName ?: ""
                            val message = "Welcome back $displayName!"
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_SHORT
                                ).show()
                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.main_container, HomeFragment())
                                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                                    .commit()
                                binding.loading.visibility = View.GONE
                                binding.btnLogin.visibility = View.VISIBLE
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    task.exception?.message.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.loading.visibility = View.GONE
                                binding.btnLogin.visibility = View.VISIBLE
                            }
                        }

                    }
                }
            }
            tvSignUp.setOnClickListener {
                replaceFragment(SignUpFragment())
            }
            tvForgotPassword.setOnClickListener {
                replaceFragment(ForgotPasswordFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_container, fragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .addToBackStack(null)
            .commit()
    }
}