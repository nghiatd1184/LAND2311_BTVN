package com.nghiatd.quanlyhocsinh.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.quanlyhocsinh.adapter.StudentAdapter
import com.nghiatd.quanlyhocsinh.controller.Controller
import com.nghiatd.quanlyhocsinh.databinding.FragmentScreen2Binding
import com.nghiatd.quanlyhocsinh.model.Student

class Screen2Fragment : Fragment() {
    private var _binding : FragmentScreen2Binding? = null
    private val binding : FragmentScreen2Binding by lazy { requireNotNull(_binding) }
    private val controller = Controller.getInstance()
    private val adapter : StudentAdapter by lazy { StudentAdapter(controller.getStudentList(), requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentScreen2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller.allStudentFilter()
        binding.apply {
            rvStudent.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@Screen2Fragment.adapter
            }
            btnAll.setOnClickListener {
                controller.allStudentFilter()
                adapter.notifyDataSetChanged()
            }
            btnExcellent.setOnClickListener {
                controller.exStudentFilter()
                adapter.notifyDataSetChanged()
            }
            btnGood.setOnClickListener {
                controller.goodStudentFilter()
                adapter.notifyDataSetChanged()
            }
            btnA.setOnClickListener {
                controller.aStudentFilter()
                adapter.notifyDataSetChanged()
            }
            btnB.setOnClickListener {
                controller.bStudentFilter()
                adapter.notifyDataSetChanged()
            }
            btnC.setOnClickListener {
                controller.cStudentFilter()
                adapter.notifyDataSetChanged()
            }
            btnD.setOnClickListener {
                controller.dStudentFilter()
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}