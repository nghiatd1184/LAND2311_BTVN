package com.example.cardflip

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cardflip.adapter.HomeVpAdapter
import com.example.cardflip.databinding.FragmentHomeBinding
import kotlin.system.exitProcess

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding: FragmentHomeBinding by lazy { requireNotNull(_binding) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val sharedPref = activity?.getSharedPreferences("appData", Context.MODE_PRIVATE)
        binding.apply {
            viewPager2.apply {
                adapter = HomeVpAdapter()
            }
            play.setOnClickListener {
                binding.apply {
                    menu.visibility = View.GONE
                    levelMenu.visibility = View.VISIBLE
                }
            }
            quit.setOnClickListener {
                exitProcess(0)
            }
            tvLvBack.setOnClickListener {
                binding.apply {
                    menu.visibility = View.VISIBLE
                    levelMenu.visibility = View.GONE
                }
            }
            tvLvEasy.setOnClickListener {
                sharedPref?.edit()?.putInt("mode", 6)?.apply()
                replaceFragment(PlayFragment())
            }
            tvLvNormal.setOnClickListener {
                sharedPref?.edit()?.putInt("mode", 16)?.apply()
                replaceFragment(PlayFragment())
            }
            tvLvHard.setOnClickListener {
                sharedPref?.edit()?.putInt("mode", 36)?.apply()
                replaceFragment(PlayFragment())
            }
        }
        autoScroll()
    }

    @Suppress("DEPRECATION")
    private fun autoScroll() {
        Handler().postDelayed({
            val nextItem = (binding.viewPager2.currentItem + 1) % 4
            binding.viewPager2.setCurrentItem(nextItem, true)
            autoScroll()
        }, 4000L)
    }

    private fun replaceFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                androidx.appcompat.R.anim.abc_tooltip_enter,
                androidx.appcompat.R.anim.abc_tooltip_exit,
            )
            .replace(R.id.container, fragment)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}