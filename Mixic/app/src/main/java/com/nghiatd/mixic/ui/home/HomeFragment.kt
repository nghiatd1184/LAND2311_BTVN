package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentHomeBinding
import com.nghiatd.mixic.ui.home.device.DeviceFragment

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        replaceFragment(HomeFirebaseFragment())
        binding.apply {
            bottomNav.setOnItemSelectedListener{
                when (it.itemId) {
                    R.id.home -> {
                        replaceFragment(HomeFirebaseFragment())
                        true
                    }
                    R.id.device -> {
                        replaceFragment(DeviceFragment())
                        true
                    }
                    R.id.search -> {
//                        replaceFragment(SearchFragment())
                        true
                    }
                    else -> {
//                        replaceFragment(ProfileFragment())
                        true
                    }
                }
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(binding.containerHome.id, fragment)
            .addToBackStack(null)
            .commit()
    }
}