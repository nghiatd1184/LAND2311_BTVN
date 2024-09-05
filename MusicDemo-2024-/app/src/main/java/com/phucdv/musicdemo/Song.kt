package com.phucdv.musicdemo

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val albumUri: Uri,
    var isPlaying: Boolean = false
)
