package com.nghiatd.mixic.data.model

data class Category(
    val id: String,
    val name: String,
    val songs : List<Song>
) {
}