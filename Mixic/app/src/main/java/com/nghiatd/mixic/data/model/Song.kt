package com.nghiatd.mixic.data.model

import java.io.Serializable

data class Song(
    val id: Long,
    val name: String,
    val artist: String,
    val duration: Long,
    val image: String,
    val data: String
) : Serializable