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
import com.nghiatd.quanlyhocsinh.listener.OnStudentClickListener

class Screen2Fragment : Fragment(), OnStudentClickListener {
    private var _binding : FragmentScreen2Binding? = null
    private val binding : FragmentScreen2Binding by lazy { requireNotNull(_binding) }
    private val controller = Controller.getInstance()
    private val adapter : StudentAdapter by lazy { StudentAdapter(controller.getStudentList(), requireContext(), this) }

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
            btnSort.setOnClickListener {
                controller.showSortDialog(requireContext(),adapter)
            }
            btnFilter.setOnClickListener {
                controller.showFilterDialog(requireContext(),adapter)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onLongClick(position: Int) {
        controller.showOnLongClickDialog(requireContext(),position,adapter)
    }
}