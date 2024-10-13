package com.nghiatd.mixic.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: SongRepository) : ViewModel() {
    private val _songsFromDevice = MutableStateFlow<List<Song>>(emptyList())
    val songsFromDevice = _songsFromDevice.asStateFlow()

    private val _songsFromFirebase = MutableStateFlow<List<Song>>(emptyList())
    val songsFromFirebase = _songsFromFirebase.asStateFlow()

    init {
        viewModelScope.launch {
            _songsFromDevice.value = repository.getAllDeviceSongs()
        }
        getAllFirebaseSong()
    }

    class SongViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(SongRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    private fun getAllFirebaseSong()  {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("song")
            .addSnapshotListener { value, _ ->
                val allSong = mutableListOf<Song>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val artist = documentSnapshot.getString("artist") ?: return@forEach
                    val image = documentSnapshot.getString("image") ?: return@forEach
                    val data = documentSnapshot.getString("data") ?: return@forEach
                    val song = Song(id, name, artist, image, data)
                    allSong.add(song)
                }
                _songsFromFirebase.value = allSong
            }
    }
}