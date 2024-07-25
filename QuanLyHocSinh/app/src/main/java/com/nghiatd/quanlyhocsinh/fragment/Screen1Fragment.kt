package com.nghiatd.quanlyhocsinh.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.nghiatd.quanlyhocsinh.controller.Controller
import com.nghiatd.quanlyhocsinh.databinding.FragmentScreen1Binding

class Screen1Fragment : Fragment() {
    private var _binding: FragmentScreen1Binding? = null
    private val binding: FragmentScreen1Binding by lazy { requireNotNull(_binding) }
    private val controller = Controller.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreen1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnSave.setOnClickListener {
                controller.addStudent(
                    requireContext(),
                    binding.edtID,
                    binding.edtName,
                    binding.edtBirthYear,
                    binding.edtMathPoint,
                    binding.edtPhysicsPoint,
                    binding.edtChemistryPoint,
                    binding.edtBiologyPoint,
                    binding.edtHistoryPoint,
                    binding.edtGeographyPoint,
                    binding.edtLiteraturePoint,
                    binding.edtEnglishPoint
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}