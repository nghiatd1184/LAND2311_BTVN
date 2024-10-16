package com.nghiatd.mixic.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.databinding.ItemSongSquareBinding

class SectionAdapter(val onItemClick: (Song) -> Unit) : ListAdapter<Song, SectionAdapter.SectionViewHolder>(object :
    DiffUtil.ItemCallback<Song>() {
    override fun areItemsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Song, newItem: Song): Boolean {
        return oldItem.id == newItem.id
    }
}) {

    var isPlaying = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var playingSong: Song? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        return SectionViewHolder(
            ItemSongSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SectionViewHolder(private val binding: ItemSongSquareBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.tvName.text = song.name
            binding.tvArtist.text = song.artist
            val uri = Uri.parse(song.image)
            if(uri != null) {
                Glide.with(binding.imgArt)
                    .load(uri)
                    .apply(RequestOptions().transform(RoundedCorners(15)))
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(binding.imgArt)
            } else {
                Glide.with(binding.imgArt)
                    .load(R.drawable.splash_img)
                    .apply(RequestOptions().transform(RoundedCorners(15)))
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(binding.imgArt)
            }

            binding.root.setOnClickListener {
                binding.tvName.isSelected = true
                onItemClick(song)
            }
            val lottieView = binding.lottiePlaying
            if(isPlaying) {
                if(playingSong == song) {
                    if(lottieView.visibility == View.GONE) {
                        lottieView.visibility = View.VISIBLE
                    } else {
                        lottieView.resumeAnimation()
                    }
                } else {
                    lottieView.visibility = View.GONE
                }
            } else {
                if(playingSong == song) {
                    lottieView.pauseAnimation()
                } else {
                    lottieView.visibility = View.GONE
                }
            }
        }
    }
}