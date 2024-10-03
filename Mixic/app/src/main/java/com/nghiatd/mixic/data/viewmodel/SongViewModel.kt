package com.nghiatd.mixic.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.repository.SongRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SongViewModel(private val repository: SongRepository) : ViewModel() {
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs = _allSongs.asStateFlow()

    init {
        viewModelScope.launch {
            _allSongs.value = repository.getAllSongs()
        }
    }

    class SongViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SongViewModel::class.java)) {
                return SongViewModel(SongRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}