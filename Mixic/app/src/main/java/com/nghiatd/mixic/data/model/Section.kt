package com.nghiatd.mixic.data.model

data class Section(
    val id: String,
    val name: String,
    val songs : List<Song>
) {
}