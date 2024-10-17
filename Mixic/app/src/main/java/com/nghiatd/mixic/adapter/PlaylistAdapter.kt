package com.nghiatd.mixic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.data.viewmodel.PlayListViewModel
import com.nghiatd.mixic.databinding.ItemPlaylistBinding

class PlaylistAdapter(val viewModel: PlayListViewModel, val onItemClick: (Playlist) -> Unit) : ListAdapter<Playlist, PlaylistAdapter.PlaylistViewHolder>(object :
        DiffUtil.ItemCallback<Playlist>() {
        override fun areItemsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Playlist, newItem: Playlist): Boolean {
            return oldItem.id == newItem.id
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(
            ItemPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class PlaylistViewHolder(private val binding: ItemPlaylistBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(playlist: Playlist) {
            binding.apply {
                tvName.text = playlist.name
                val numberText = StringBuilder()
                numberText.append("${playlist.songs.size} songs")
                tvNumber.text = numberText
                val image = if (playlist.songs.isEmpty()) R.drawable.logo else playlist.songs[0].image
                Glide.with(imgArt)
                    .load(image)
                    .transition(DrawableTransitionOptions.withCrossFade(500))
                    .into(imgArt)
                root.setOnClickListener {
                    onItemClick(playlist)
                }
            }
        }
    }
}