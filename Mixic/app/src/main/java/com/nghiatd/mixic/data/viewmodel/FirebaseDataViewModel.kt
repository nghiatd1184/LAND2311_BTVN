package com.nghiatd.mixic.data.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FirebaseDataViewModel : ViewModel() {
    private val _allFeature = MutableStateFlow<List<Feature>>(emptyList())
    val allFeature = _allFeature.asStateFlow()

    private val _allCategory = MutableStateFlow<List<Category>>(emptyList())
    val allCategory = _allCategory.asStateFlow()

    init {
        getAllCategory()
        getAllFeature()
    }

    private fun getAllFeature() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("feature")
            .addSnapshotListener { value, error ->
                val allFeature = mutableListOf<Feature>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val image = documentSnapshot.getString("image") ?: return@forEach
                    val songList = documentSnapshot.get("songList") as? Array<Song>
                    val feature = Feature(id, name, image, songList?.toList() ?: emptyList())
                    allFeature.add(feature)
                }
                _allFeature.value = allFeature
                }
    }

    private fun getAllCategory() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("category")
            .addSnapshotListener { value, error ->
                val allCategory = mutableListOf<Category>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val image = documentSnapshot.getString("image") ?: return@forEach
                    val songList = documentSnapshot.get("songList") as? Array<Song>
                    val category = Category(id, name, image, songList?.toList() ?: emptyList())
                    allCategory.add(category)
                }
                _allCategory.value = allCategory
            }
    }
}