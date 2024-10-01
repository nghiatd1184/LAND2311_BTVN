package com.nghiatd.rhythmtune.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
    override fun getItemCount(): Int = 5
    override fun createFragment(position: Int): Fragment {
        TODO("Not yet implemented")
    }

}