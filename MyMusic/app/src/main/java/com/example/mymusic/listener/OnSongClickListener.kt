package com.example.mymusic.listener

import com.example.mymusic.model.Song

interface OnSongClickListener {
    fun onSongClick(song: Song, position: Int)
}