package com.nghiatd.mixic.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayListViewModel(context: Context) : ViewModel() {

    private val _devicePlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val devicePlaylists = _devicePlaylists.asStateFlow()

    private val sharedPref = context.getSharedPreferences("data", Context.MODE_PRIVATE)

    init {
        _devicePlaylists.value = getAllDevicePlaylists()
    }

    fun getAllDevicePlaylists(): List<Playlist> {
        val jsonString = sharedPref.getString("playlists", null) ?: return emptyList()
        val type = object : TypeToken<List<Playlist>>() {}.type
        val playlists = Gson().fromJson<List<Playlist>>(jsonString, type)
        return playlists
    }
    private fun saveDevicePlaylists(playlists: List<Playlist>) {
        val json = Gson().toJson(playlists)
        sharedPref.edit().putString("playlists", json).apply()
    }

    fun deleteDevicePlaylists(playlist: Playlist) {
        val playlists = devicePlaylists.value.toMutableList()
        playlists.remove(playlist)
        _devicePlaylists.value = playlists
        saveDevicePlaylists(playlists)
    }

    fun deleteSongFromPlaylist(playlist: Playlist, song: Song) {
        val playlists = devicePlaylists.value.toMutableList()
        val index = playlists.indexOf(playlist)
        playlists[index].songs.remove(song)
        _devicePlaylists.value = playlists
        saveDevicePlaylists(playlists)
    }

    fun addSongToPlaylist(playlist: Playlist?, name: String, song: Song) {
        val playlists = devicePlaylists.value.toMutableList()
        if (playlist == null) {
            playlists.add(Playlist(System.currentTimeMillis().toString(),name, mutableListOf(song)))
        } else {
            val index = playlists.indexOf(playlist)
            playlists[index].songs.add(song)
        }
        _devicePlaylists.value = playlists
        saveDevicePlaylists(playlists)
    }

    class PlayListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PlayListViewModel::class.java)) {
                return PlayListViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}