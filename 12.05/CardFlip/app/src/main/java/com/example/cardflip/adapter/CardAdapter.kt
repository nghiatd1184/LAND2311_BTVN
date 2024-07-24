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
    private val listener: PlayFragment,
    private val context : Context
) :
    RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun getItemCount(): Int = cards.size

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(position)
        setAnimation(holder.binding.root, position)
    }

    inner class CardViewHolder(val binding: ItemCardBinding) :
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

    private fun setAnimation(viewToAnimate: View, position: Int) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation : Animation
            if (position < cards.size.toDouble()/2) {
                animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_slide_in_top)
            } else {
                animation = AnimationUtils.loadAnimation(
                    context,
                    androidx.appcompat.R.anim.abc_slide_in_bottom
                )
            }
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }
}