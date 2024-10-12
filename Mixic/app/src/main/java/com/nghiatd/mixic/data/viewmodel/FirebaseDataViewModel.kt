package com.nghiatd.mixic.data.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
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

    private val _allSong = MutableStateFlow<List<Song>>(emptyList())
    val allSong = _allSong.asStateFlow()

    init {
        getAllCategory()
        getAllFeature()
        getAllSong()
    }

    private fun getAllFeature() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("feature")
            .addSnapshotListener { value, _ ->
                val allFeature = mutableListOf<Feature>()
                val tasks = mutableListOf<Task<DocumentSnapshot>>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val image = documentSnapshot.getString("image") ?: return@forEach
                    val songsRefs = documentSnapshot.get("songs") as? List<DocumentReference>
                    val songs = mutableListOf<Song>()
                    songsRefs?.forEach { songRef ->
                        songRef.get().addOnSuccessListener { _ ->
                            val task = songRef.get().addOnSuccessListener { songSnapshot ->
                                songs.add(songSnapshot.toObject(Song::class.java) ?: return@addOnSuccessListener)
                            }
                            tasks.add(task)
                        }
                    }
                    Tasks.whenAllComplete(tasks).addOnCompleteListener {
                        songs.sortBy { it.name }
                        val feature = Feature(id, name,image, songs)
                        allFeature.add(feature)
                    }
                }
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _allFeature.value = allFeature
                }
                }
    }


    private fun getAllCategory() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("category")
            .addSnapshotListener { value, _ ->
                val allCategory = mutableListOf<Category>()
                val tasks = mutableListOf<Task<DocumentSnapshot>>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val songsRefs = documentSnapshot.get("songs") as? List<DocumentReference>
                    val songs = mutableListOf<Song>()
                    songsRefs?.forEach { songRef ->
                        val task = songRef.get().addOnSuccessListener { songSnapshot ->
                            songs.add(songSnapshot.toObject(Song::class.java) ?: return@addOnSuccessListener)
                        }
                        tasks.add(task)
                    }
                    Tasks.whenAllComplete(tasks).addOnCompleteListener {
                        songs.sortBy { it.name }
                        val category = Category(id, name, songs)
                        allCategory.add(category)
                    }
                }
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _allCategory.value = allCategory
                }
            }
    }

    private fun getAllSong() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("song")
            .addSnapshotListener { value, _ ->
                val allSong = mutableListOf<Song>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val image = documentSnapshot.getString("image") ?: return@forEach
                    val url = documentSnapshot.getString("url") ?: return@forEach
                    val song = Song(id, name, image, url)
                    allSong.add(song)
                }
                _allSong.value = allSong
            }
    }
}