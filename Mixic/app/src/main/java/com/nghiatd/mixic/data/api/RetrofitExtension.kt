package com.nghiatd.mixic.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitExtension {
    private val RETROFIT: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.lyrics.ovh/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val OVH_LYRICS: OvhLyricsApi by lazy {
        RETROFIT.create(OvhLyricsApi::class.java)
    }
}