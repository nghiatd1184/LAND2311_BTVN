package com.nghiatd.quanlyhocsinh.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.nghiatd.quanlyhocsinh.adapter.StudentAdapter
import com.nghiatd.quanlyhocsinh.databinding.FragmentScreen2Binding
import com.nghiatd.quanlyhocsinh.model.Student

class Screen2Fragment : Fragment() {
    private var _binding : FragmentScreen2Binding? = null
    private val binding : FragmentScreen2Binding by lazy { requireNotNull(_binding) }
    private val studentList = arrayListOf<Student>()
    private val adapter : StudentAdapter by lazy { StudentAdapter(studentList, requireContext()) }

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
        val student1 = Student(1111,"Adam",1999,10,10,10,10,10,10,10,10,10,10,10,10, 10)
        studentList.add(student1)
        binding.apply {
            rvStudent.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = this@Screen2Fragment.adapter
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}