package com.example.cardflip

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cardflip.controller.Controller
import com.example.cardflip.databinding.FragmentEasyBinding

class EasyFragment : Fragment() {
    private var _binding : FragmentEasyBinding? = null
    private val binding : FragmentEasyBinding by lazy { requireNotNull(_binding) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEasyBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val controller = Controller.getInstance()
        controller.shuffleList()

        binding.apply {
            btnBack.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment())
                    .commit()
            }

            img1.setOnClickListener {
                val resID = controller.cardClick(1)
                if (resID != 0) {
                    binding.img1.setImageResource(resID)
                } else {

                }
            }
            img2.setOnClickListener {
                val resID = controller.cardClick(2)
                if (resID != 0) {
                    binding.img2.setImageResource(resID)
                } else {

                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}