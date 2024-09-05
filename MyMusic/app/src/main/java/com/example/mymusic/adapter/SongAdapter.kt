package com.example.mymusic.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymusic.R
import com.example.mymusic.databinding.ItemSongBinding
import com.example.mymusic.listener.OnSongClickListener
import com.example.mymusic.model.Song

class SongAdapter(
    private val songs: List<Song>,
    private val onSongClickListener: OnSongClickListener,
    private val context: Context
) : RecyclerView.Adapter<SongAdapter.SongViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongAdapter.SongViewHolder {
        val binding = ItemSongBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SongViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SongAdapter.SongViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = songs.size

    inner class SongViewHolder(private val binding: ItemSongBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            val song = songs[position]
            binding.apply {
                tvTitle.text = song.title
                tvArtist.text = song.artist
                try {
//                    img.setImageURI(song.albumUri)
                    Glide.with(context).load(song.albumUri).into(binding.img)
                } catch (e: Exception) {
                    e.printStackTrace()
                    img.setImageResource(R.drawable.img_demo)
                }

                if (song.isPlaying) {
                    lottiePlaying.visibility = View.VISIBLE
                    root.setBackgroundResource(R.drawable.shape_item_playing)
                } else {
                    lottiePlaying.visibility = View.GONE
                    root.setBackgroundResource(R.drawable.shape_item_non_playing)
                }
                root.setOnClickListener {
                    onSongClickListener.onSongClick(song, position)
                }
            }
        }
    }
}