package com.nghiatd.mixic.data.viewmodel

import android.util.Log
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

    init {
        getAllCategory()
        getAllFeature()
    }

    private fun getAllFeature() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("feature")
            .addSnapshotListener { value, error ->
                val allFeature = mutableListOf<Feature>()
                val tasks = mutableListOf<Task<DocumentSnapshot>>()
                value?.documents?.forEach { documentSnapshot ->
                    val id = documentSnapshot.id
                    val name = documentSnapshot.getString("name") ?: return@forEach
                    val image = documentSnapshot.getString("image") ?: return@forEach
                    val songsRefs = documentSnapshot.get("songs") as? List<DocumentReference>
                    val songs = mutableListOf<Song>()
                    songsRefs?.forEach { songRef ->
                        songRef.get().addOnSuccessListener { songSnapshot ->
                            val task = songRef.get().addOnSuccessListener { songSnapshot ->
                                val songId = songSnapshot.id
                                val songName =
                                    songSnapshot.getString("name") ?: return@addOnSuccessListener
                                val songArtist =
                                    songSnapshot.getString("artist") ?: return@addOnSuccessListener
                                val songImage =
                                    songSnapshot.getString("image") ?: return@addOnSuccessListener
                                val songData =
                                    songSnapshot.getString("data") ?: return@addOnSuccessListener
                                val song = Song(songId, songName, songArtist, songImage, songData)
                                songs.add(song)
                            }
                            tasks.add(task)
                        }
                    }
                    val feature = Feature(id, name, image, songs)
                    allFeature.add(feature)
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
                            val songId = songSnapshot.id
                            val songName =
                                songSnapshot.getString("name") ?: return@addOnSuccessListener
                            val songArtist =
                                songSnapshot.getString("artist") ?: return@addOnSuccessListener
                            val songImage =
                                songSnapshot.getString("image") ?: return@addOnSuccessListener
                            val songData =
                                songSnapshot.getString("data") ?: return@addOnSuccessListener
                            val song = Song(songId, songName, songArtist, songImage, songData)
                            songs.add(song)
                        }
                        tasks.add(task)
                    }
                    val category = Category(id, name, songs = songs)
                    allCategory.add(category)
                }
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _allCategory.value = allCategory
                }
            }
    }
}