package com.nghiatd.mixic.data.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.model.Song
import com.nghiatd.mixic.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedDataViewModel : ViewModel() {

    private val _selectedFeature = MutableStateFlow<Feature?>(null)
    val selectedFeature = _selectedFeature.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedSection = MutableStateFlow<Section?>(null)
    val selectedSection = _selectedSection.asStateFlow()

    private val _selectedSong = MutableStateFlow<Song?>(null)
    val selectedSong = _selectedSong.asStateFlow()

    fun setSong(song: Song?) {
        Log.d("NGHIA", "setSong $song")
        _selectedSong.value = song
        Log.d("NGHIA", "_selectedSong ${selectedSong.value}")
    }

    fun setFeature(feature: Feature?) {
        _selectedFeature.value = feature
    }

    fun setCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun setSection(section: Section?) {
        _selectedSection.value = section
    }
}