package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nghiatd.mixic.R
import com.nghiatd.mixic.databinding.FragmentHomeFirebaseBinding

class HomeFirebaseFragment : Fragment() {

    private lateinit var binding: FragmentHomeFirebaseBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeFirebaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        val bottomNav = (parentFragment as HomeFragment).view?.findViewById<View>(R.id.bottom_nav)
        binding.apply {


        }
    }
}