package com.nghiatd.mixic.data.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.Normalizer

class SearchViewModel(private val repository: SongRepository) : ViewModel() {
    private val _resultFromDevice = MutableStateFlow<List<Song>>(emptyList())
    val resultFromDevice = _resultFromDevice.asStateFlow()

    private val _resultFromFirebase = MutableStateFlow<List<Song>>(emptyList())
    val resultFromFirebase = _resultFromFirebase.asStateFlow()

    fun searchSongFromDevice(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val songs = repository.searchSongFromDevice(query)
            _resultFromDevice.value = songs
        }
    }

    fun searchSongFromFirebase(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val db = FirebaseFirestore.getInstance()
            val normalizedQuery = query.normalize()
            val songCollection = db.collection("song")
            val searchQuery = songCollection.get().await()
            val filterList = searchQuery.documents.mapNotNull { it.toObject(Song::class.java)!! }
                .filter {
                    it.name.normalize().contains(normalizedQuery, ignoreCase = true) || it.artist.normalize().contains(normalizedQuery, ignoreCase = true)
                }
            _resultFromFirebase.value = filterList

        }
    }

    private fun String.normalize(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    class SongViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                return SearchViewModel(SongRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}