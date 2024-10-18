package com.nghiatd.mixic.data.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.repository.SongRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DeviceViewModel(private val repository: SongRepository) : ViewModel() {
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs = _allSongs.asStateFlow()

    init {
        loadAllSongs()
    }

    fun loadAllSongs() {
        viewModelScope.launch {
            _allSongs.value = repository.getAllDeviceSongs()
        }
    }

    class SongViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DeviceViewModel::class.java)) {
                return DeviceViewModel(SongRepository(context)) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}