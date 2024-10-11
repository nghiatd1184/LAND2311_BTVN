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
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.mixic.R
import com.nghiatd.mixic.adapter.CategoryAdapter
import com.nghiatd.mixic.adapter.FeatureAdapter
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.data.viewmodel.FirebaseDataViewModel
import com.nghiatd.mixic.data.viewmodel.SharedDataViewModel
import com.nghiatd.mixic.databinding.FragmentHomeFirebaseBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFirebaseFragment : Fragment() {

    private lateinit var binding: FragmentHomeFirebaseBinding
    private lateinit var firebaseViewModel: FirebaseDataViewModel
    private lateinit var sharedViewModel: SharedDataViewModel
    private val featureList = mutableListOf<Feature>()
    private val categoryList = mutableListOf<Category>()
    private val featureAdapter : FeatureAdapter by lazy { FeatureAdapter(featureList) }
    private val categoryAdapter : CategoryAdapter by lazy { CategoryAdapter(categoryList) {category ->
        Log.d("NGHIA_CHECK", "category: $category")
        sharedViewModel.setCategory(category)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, SongListFragment())
            .addToBackStack(null)
            .commit()
    }}
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
        sharedViewModel = ViewModelProvider(requireActivity())[SharedDataViewModel::class.java]
        firebaseViewModel = ViewModelProvider(requireActivity())[FirebaseDataViewModel::class.java]
        binding = FragmentHomeFirebaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        initViewFeature()
        initViewCategory()
        handler.postDelayed(autoScrollRunnable, 3000)

    }

    private fun initViewFeature() {
        binding.viewPagerFeature.apply {
            adapter = featureAdapter
            offscreenPageLimit = 3
        }
        lifecycleScope.launch {
            firebaseViewModel.allFeature.collectLatest {
                featureList.clear()
                featureList.addAll(it)
                featureAdapter.notifyItemRangeInserted(0, it.size)
            }
        }
    }

    private fun initViewCategory() {
        binding.rvCategory.apply {
            adapter = categoryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
        lifecycleScope.launch {
            firebaseViewModel.allCategory.collectLatest {
                categoryList.clear()
                categoryList.addAll(it)
                categoryAdapter.notifyItemRangeInserted(0, it.size)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(autoScrollRunnable)
    }
}