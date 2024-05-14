package com.example.cardflip.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.cardflip.R
import com.example.cardflip.databinding.ItemImageBinding

class HomeVpAdapter() : RecyclerView.Adapter<HomeVpAdapter.ImageViewHolder>() {
    inner class ImageViewHolder(private val binding : ItemImageBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(position: Int) {
            when (position) {
                0 -> binding.img.setImageResource(R.drawable.jianxin)
                1 -> binding.img.setImageResource(R.drawable.lingyang)
                2 -> binding.img.setImageResource(R.drawable.weilinai)
                else -> binding.img.setImageResource(R.drawable.anke)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val binding = ItemImageBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ImageViewHolder(binding)
    }

    override fun getItemCount(): Int = 4

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(position)
    }
}