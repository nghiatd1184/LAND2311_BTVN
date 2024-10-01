package com.nghiatd.mixic.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentSplashBinding
import com.nghiatd.mixic.ui.home.HomeFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {
    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()

        lifecycleScope.launch {
            delay(2000)
//            val user = FirebaseAuth.getInstance().currentUser
            replaceFragment(HomeFragment())
//            user?.let {
//
//            } ?: run {
//                replaceFragment(LogInFragment())
//            }
        }
    }

    private fun initView() {
        Glide.with(this)
            .load(R.drawable.logo)
            .apply(RequestOptions().override(200, 200))
            .into(binding.imgLogo)

        Glide.with(this)
            .load(R.drawable.splash_img)
            .into(binding.imgSplash)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }
}