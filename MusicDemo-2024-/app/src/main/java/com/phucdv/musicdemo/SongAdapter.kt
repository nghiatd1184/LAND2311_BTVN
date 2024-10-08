package com.phucdv.musicdemo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.phucdv.musicdemo.databinding.ItemSongBinding

class SongAdapter(
    private val songs: List<Song>,
    private val onSongClickListener: OnSongClickListener
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
            Log.d("NGHIA", "bind: $song")
            binding.apply {
                tvTitle.text = song.title
                tvTitle.requestFocus()
                tvTitle.isSelected = true
                tvArtist.text = song.artist
                try {
                    img.setImageURI(song.albumUri)
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