package vn.study.qlchsach.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.room.Room
import vn.study.qlchsach.QLDatabase
import vn.study.qlchsach.databinding.FragmentBooksBinding
import vn.study.qlchsach.databinding.FragmentPublisherBinding
import vn.study.qlchsach.entity.Publisher

class PublisherFragment : Fragment() {

    private var _binding: FragmentPublisherBinding? = null
    private val binding: FragmentPublisherBinding
        get() {
            return _binding!!
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPublisherBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSave.setOnClickListener {
            QLDatabase.getInstance(requireActivity()).getPublisherDao().insertItem(
                Publisher(
                    name = binding.edtName.text.toString(), address = binding.edtAddress.text.toString()
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}