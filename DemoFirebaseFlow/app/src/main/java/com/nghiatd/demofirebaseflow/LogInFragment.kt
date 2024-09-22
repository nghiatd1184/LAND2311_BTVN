package com.nghiatd.demofirebaseflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.nghiatd.demofirebaseflow.databinding.FragmentLogInBinding
import kotlinx.coroutines.launch


class LogInFragment : Fragment() {

    private lateinit var binding: FragmentLogInBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLogInBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        lifecycleScope.launch {
            binding.btnSignIn.setOnClickListener{

            }
        }
    }

    private fun initViews() {
        binding.apply {
            tvSignUp.setOnClickListener {
                replaceFragment(RegisterFragment())
            }
            tvForgotPassword.setOnClickListener {
                replaceFragment(ForgotPasswordFragment())
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack("LogInFragment")
            .commit()
    }


}