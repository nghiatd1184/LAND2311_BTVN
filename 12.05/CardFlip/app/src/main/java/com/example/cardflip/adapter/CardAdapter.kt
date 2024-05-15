package com.example.cardflip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.cardflip.PlayFragment
import com.example.cardflip.R
import com.example.cardflip.databinding.ItemCardBinding


class CardAdapter(
    private val cards: List<Int>,
    private val listener: PlayFragment
) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class CardViewHolder(private val binding: ItemCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                img.setImageResource(cards[position])
                root.setOnClickListener {
                    listener.onCardClick(cards[position],position)
                }
            }
        }
    }
}