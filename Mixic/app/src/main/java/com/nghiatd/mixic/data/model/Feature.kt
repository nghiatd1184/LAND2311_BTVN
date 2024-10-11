package com.nghiatd.mixic.data.model

data class Feature(
    val id: String,
    val name: String,
    val image: String,
    val songs : List<Song>
) {
}