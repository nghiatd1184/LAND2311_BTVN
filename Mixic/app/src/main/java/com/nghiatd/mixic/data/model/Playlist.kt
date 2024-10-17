package com.nghiatd.mixic.data.model

data class Playlist(
    val id: String = "",
    val name: String = "",
    val songs: MutableList<Song> = mutableListOf()
)
