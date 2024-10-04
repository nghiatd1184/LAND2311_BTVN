package com.nghiatd.mixic.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.databinding.ItemSongListBinding

class DeviceSongAdapter(val onItemClick: (Song) -> Unit) : ListAdapter<Song, DeviceSongAdapter.DeviceSongViewHolder>(object :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceSongViewHolder {
        return DeviceSongViewHolder(
            ItemSongListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: DeviceSongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DeviceSongViewHolder(val binding: ItemSongListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.tvName.text = song.name
            binding.tvArtist.text = song.artist
            val uri = Uri.parse(song.image)
            if(uri != null) {
                Glide.with(binding.imgArt)
                    .load(uri)
                    .apply(RequestOptions().transform(RoundedCorners(15)))
                    .into(binding.imgArt)
            } else {
                Glide.with(binding.imgArt)
                    .load(R.drawable.splash_img)
                    .apply(RequestOptions().transform(RoundedCorners(15)))
                    .into(binding.imgArt)
            }

            binding.root.setOnClickListener {
                onItemClick(song)
            }

            if(isPlaying) {
                if(playingSong == song) {
                    binding.lottiePlaying.visibility = View.VISIBLE
                } else {
                    binding.lottiePlaying.visibility = View.GONE
                }
            } else {
                if(playingSong == song) {
                    binding.lottiePlaying.pauseAnimation()
                } else {
                    binding.lottiePlaying.visibility = View.GONE
                }
            }
        }
    }
}