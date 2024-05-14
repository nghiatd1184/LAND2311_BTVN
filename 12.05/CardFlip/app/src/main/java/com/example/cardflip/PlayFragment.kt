package com.example.cardflip

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cardflip.adapter.CardAdapter
import com.example.cardflip.controller.Controller
import com.example.cardflip.databinding.FragmentPlayBinding
import com.example.cardflip.listener.OnCardClickListener

class PlayFragment : Fragment(), OnCardClickListener {
    private var _binding: FragmentPlayBinding? = null
    private val binding: FragmentPlayBinding by lazy { requireNotNull(_binding) }
    private val controller = Controller.getInstance()
    private val cards: ArrayList<Int> by lazy { controller.getCards() }
    private val cardAdapter: CardAdapter by lazy {
        CardAdapter(cards, this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Lấy dữ liệu độ khó
        val sharedPref = activity?.getSharedPreferences("appData", Context.MODE_PRIVATE)
        controller.setGameMote(sharedPref!!.getInt("mode", 0))
        //Chuẩn bị data game
        controller.prepareData()

        binding.apply {
            btnBack.setOnClickListener {
                controller.clear()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.container, HomeFragment())
                    .commit()
            }

            cards.apply {
                layoutManager =
                    object : GridLayoutManager(requireContext(), controller.getLayoutColumn()) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                adapter = cardAdapter
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardClick(resId: Int, position: Int) {
        if (!controller.isCompare()) {
            if (resId == R.drawable.backside) {
                val count = controller.cardClick(position)
                cardAdapter.notifyDataSetChanged()
                if (count >= 2) {
                    when (controller.getLifeCount()) {
                        1 -> controller.compareCard(cardAdapter, binding.lifeCoin1)
                        2 -> controller.compareCard(cardAdapter, binding.lifeCoin2)
                        3 -> controller.compareCard(cardAdapter, binding.lifeCoin3)
                    }

                }
            }
        }
    }
}