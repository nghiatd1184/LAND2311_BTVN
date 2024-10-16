package com.nghiatd.mixic.adapter

import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.core.content.ContextCompat.getString
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.databinding.ItemSongListBinding
import java.io.File
import java.io.FileNotFoundException

class SongAdapter(val onItemClick: (Song) -> Unit) :
    ListAdapter<Song, SongAdapter.SongViewHolder>(object :
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        return SongViewHolder(
            ItemSongListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SongViewHolder(private val binding: ItemSongListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(song: Song) {
            binding.tvName.isSelected = true

            binding.tvName.text = song.name
            binding.tvArtist.text = song.artist
            val uri = Uri.parse(song.image)

            Glide.with(binding.imgArt)
                .load(uri)
                .apply(RequestOptions().transform(RoundedCorners(15)))
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .error(R.mipmap.ic_launcher)
                .into(binding.imgArt)

            binding.root.setOnClickListener {
                onItemClick(song)
            }

            binding.imgMenu.setOnClickListener { view ->
                val popupMenu = PopupMenu(view.context, view)
                popupMenu.menuInflater.inflate(R.menu.menu_song_options, popupMenu.menu)
                popupMenu.setOnMenuItemClickListener { item ->
                    when (item.itemId) {
                        R.id.action_download -> {
                            downloadSong(song, view)
                            true
                        }

                        R.id.action_details -> {
                            Toast.makeText(view.context, "Details", Toast.LENGTH_SHORT).show()
                            true
                        }

                        else -> false
                    }
                }
                popupMenu.show()
            }

            val lottieView = binding.lottiePlaying
            if (isPlaying) {
                if (playingSong == song) {
                    if (lottieView.visibility == View.GONE) {
                        lottieView.visibility = View.VISIBLE
                    } else {
                        lottieView.resumeAnimation()
                    }
                } else {
                    lottieView.visibility = View.GONE
                }
            } else {
                if (playingSong == song) {
                    lottieView.pauseAnimation()
                } else {
                    lottieView.visibility = View.GONE
                }
            }
        }
    }

    private fun downloadSong(song: Song, view: View) {
        try {
            song.id.toInt()
            Toast.makeText(
                view.context,
                getString(view.context, R.string.download_already_exist),
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            val storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.data)
            val localFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                "${song.name} - ${song.artist}.mp3"
            )

            storageRef.getFile(localFile).addOnSuccessListener {
                Toast.makeText(
                    view.context,
                    getString(view.context, R.string.download_success),
                    Toast.LENGTH_SHORT
                ).show()
            }.addOnFailureListener {
                Toast.makeText(
                    view.context,
                    getString(view.context, R.string.download_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}