package com.nghiatd.mixic.data.api

import com.nghiatd.mixic.data.model.LyricsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface OvhLyricsApi {
    @GET("v1/{artist}/{title}")
    fun getLyrics(
        @Path("artist") artist: String,
        @Path("title") title: String
    ) : Call<LyricsResponse>
}