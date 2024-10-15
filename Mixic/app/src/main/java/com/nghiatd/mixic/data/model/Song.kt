package com.nghiatd.mixic.data.model

import com.google.firebase.Timestamp

data class Song(
    val id: String = "",
    val name: String = "",
    val artist: String = "",
    val image: String = "",
    val data: String = "",
    val time: Timestamp = Timestamp.now()
) {
}