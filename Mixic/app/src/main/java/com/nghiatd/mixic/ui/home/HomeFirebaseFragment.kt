package com.nghiatd.mixic.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.FeatureAdapter
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.data.viewmodel.FirebaseDataViewModel
import com.nghiatd.mixic.databinding.FragmentHomeFirebaseBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFirebaseFragment : Fragment() {

    private lateinit var binding: FragmentHomeFirebaseBinding
    private lateinit var viewModel: FirebaseDataViewModel
    private val featureList = mutableListOf<Feature>()
    private val featureAdapter : FeatureAdapter by lazy { FeatureAdapter(featureList) }
    private val handler = Handler(Looper.getMainLooper())
    private val autoScrollRunnable = object : Runnable {
        override fun run() {
            val itemCount = featureAdapter.itemCount
            if (itemCount > 0) {
                val nextItem = (binding.viewPagerFeature.currentItem + 1) % itemCount
                binding.viewPagerFeature.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 3000) // Change slide every 3 seconds
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this)[FirebaseDataViewModel::class.java]
        binding = FragmentHomeFirebaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initViewFeature()
        handler.postDelayed(autoScrollRunnable, 3000)

    }

    private fun initViewFeature() {
        binding.viewPagerFeature.apply {
            adapter = featureAdapter
            offscreenPageLimit = 3
        }
        lifecycleScope.launch {
            viewModel.allFeature.collectLatest {
                featureList.clear()
                featureList.addAll(it)
                featureAdapter.notifyItemRangeInserted(0, it.size)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
    }
}