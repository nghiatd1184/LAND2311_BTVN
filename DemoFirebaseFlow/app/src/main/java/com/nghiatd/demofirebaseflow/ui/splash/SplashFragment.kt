package com.nghiatd.demofirebaseflow.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.demofirebaseflow.R
import com.nghiatd.demofirebaseflow.databinding.FragmentSplashBinding
import com.nghiatd.demofirebaseflow.ui.home.HomeFragment
import com.nghiatd.demofirebaseflow.ui.login.LogInFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            delay(2000)
            val user = FirebaseAuth.getInstance().currentUser
            user?.let {
                replaceFragment(HomeFragment())
            } ?: run {
                replaceFragment(LogInFragment())
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}