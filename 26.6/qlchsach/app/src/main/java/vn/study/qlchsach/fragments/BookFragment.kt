package vn.study.qlchsach.fragments

import android.R
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import vn.study.qlchsach.QLDatabase
import vn.study.qlchsach.databinding.FragmentBooksBinding
import vn.study.qlchsach.entity.Publisher
import kotlin.concurrent.thread

class BookFragment : Fragment() {

    private var _binding: FragmentBooksBinding? = null
    private val binding: FragmentBooksBinding
        get() {
            return _binding!!
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


//        thread {
//            val publisherNames = arrayListOf<String>()
//            publisherNames.addAll(QLDatabase.getInstance(requireActivity()).getPublisherDao().getAll().map { it.name })
//            if (publisherNames.isEmpty()) {
//                publisherNames.add("Chưa có nhà PH")
//            }
//            requireActivity().runOnUiThread {
//
//            }
//        }


        val publisherNames = arrayListOf<String>()
        publisherNames.addAll(QLDatabase.getInstance(requireActivity()).getPublisherDao()
            .getAll()/* return list Publisher */
//            .getAllReturnName()
            /*  T: Publisher */
            /*  R: String */
            .map { it.name }) /* return list String */
//
//        val a = Publisher(name = "1", address = "2").apply {
//            isSelected = true
//        }
//        val c = Publisher(name = "3", address = "4").also {
//            it.isSelected
//        }
//        val b = Publisher(name = "", address = "").let {
//            it.isSelected
//        }

//        Log.d("aaaa", a.name) // a là Pubisher
//        Log.d("aaaa", b.toString()) // b là boolean

//        QLDatabase.getInstance(requireActivity()).getPublisherDao().getAll().forEach {
//            publisherNames.add(it.name)
//        }

        if (publisherNames.isEmpty()) {
            publisherNames.add("Chưa có nhà PH")
        }

        val adapterDM: ArrayAdapter<String> =
            ArrayAdapter<String>(requireContext(), R.layout.simple_spinner_item, publisherNames)
        adapterDM.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinnerPublisher.adapter = adapterDM
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}