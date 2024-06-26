package com.example.cardflip

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.cardflip.adapter.CardAdapter
import com.example.cardflip.controller.MainController
import com.example.cardflip.databinding.FragmentPlayBinding
import com.example.cardflip.listener.OnCardClickListener

class PlayFragment : Fragment(), OnCardClickListener {
    private var _binding: FragmentPlayBinding? = null
    private val binding: FragmentPlayBinding by lazy { requireNotNull(_binding) }
    private val mainController = MainController.getInstance()
    private val cards: ArrayList<Int> by lazy { mainController.getCards() }
    private val cardAdapter: CardAdapter by lazy {
        CardAdapter(cards, this, requireContext())
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
        mainController.clearControllerData()
        val sharedPref = activity?.getSharedPreferences("appData", Context.MODE_PRIVATE)
        mainController.setGameMote(sharedPref!!.getInt("mode", 0))
        mainController.prepareData()

        binding.apply {
            btnBack.setOnClickListener {
                mainController.clearControllerData()
                parentFragmentManager.beginTransaction()
                    .setCustomAnimations(
                        androidx.appcompat.R.anim.abc_tooltip_enter,
                        androidx.appcompat.R.anim.abc_tooltip_exit,
                    )
                    .replace(R.id.container, HomeFragment())
                    .commitNow()
            }
            btnRestart.setOnClickListener {
                binding.playTime.text = "0"
                binding.btnRestart.isClickable = false
                binding.apply {
                    lifeCoin1.isVisible = true
                    lifeCoin2.isVisible = true
                    lifeCoin3.isVisible = true
                }
                mainController.restart()
                cardAdapter.notifyDataSetChanged()
                @Suppress("DEPRECATION")
                Handler().postDelayed({
                    mainController.flipBackSide(binding.playTime)
                    cardAdapter.notifyDataSetChanged()
                    binding.btnRestart.isClickable = true
                }, 6000L)
            }
            rvCards.apply {
                layoutManager =
                    object : GridLayoutManager(requireContext(), mainController.getLayoutColumn()) {
                        override fun canScrollVertically(): Boolean {
                            return false
                        }
                    }
                adapter = cardAdapter
            }
        }
        binding.btnRestart.isClickable = false
        @Suppress("DEPRECATION")
        Handler().postDelayed({
            mainController.flipBackSide(binding.playTime)
            cardAdapter.notifyDataSetChanged()
            binding.btnRestart.isClickable = true
        }, 6000L)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardClick(resId: Int, position: Int) {
        Log.d("DMM","$resId + $position")
        if (!mainController.isCompare()) {
            if (resId == R.drawable.backside) {
                val count = mainController.cardClick(cardAdapter, position)
                Log.d("DMM","$count")
                if (count >= 2) {
                    var msg: String = ""
                    when (mainController.getLifeCount()) {
                        1 -> msg = mainController.compareCard(cardAdapter, binding.lifeCoin1)
                        2 -> msg = mainController.compareCard(cardAdapter, binding.lifeCoin2)
                        3 -> msg = mainController.compareCard(cardAdapter, binding.lifeCoin3)
                    }
                    if (msg.isNotEmpty()) {
                        mainController.stopTimer()
                        showCustomDialog(msg)
                    }
                }
            }
        }
    }

    private fun showCustomDialog(msg: String) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val tvMsg: TextView = dialog.findViewById(R.id.msg)
        val btn: TextView = dialog.findViewById(R.id.btn)
        tvMsg.text = msg
        btn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }
}