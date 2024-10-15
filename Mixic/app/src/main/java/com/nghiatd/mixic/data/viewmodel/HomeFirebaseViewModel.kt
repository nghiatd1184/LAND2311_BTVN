package com.nghiatd.mixic.data.viewmodel

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Feature
import com.nghiatd.mixic.data.model.Section
import com.nghiatd.mixic.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeFirebaseViewModel : ViewModel() {
    private val _allFeature = MutableStateFlow<List<Feature>>(emptyList())
    val allFeature = _allFeature.asStateFlow()

    private val _allCategory = MutableStateFlow<List<Category>>(emptyList())
    val allCategory = _allCategory.asStateFlow()


    private val _allSection = MutableStateFlow<List<Section>>(emptyList())
    val allSection = _allSection.asStateFlow()

    private val _newSongs = MutableStateFlow<List<Song>>(emptyList())
    val newSongs = _newSongs.asStateFlow()

    init {
        getAllCategory()
        getAllFeature()
        getAllSection()
        get10NewSongs()
    }

    private fun get10NewSongs() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("song")
            .orderBy("time", Direction.DESCENDING)
            .limit(10)
            .addSnapshotListener { value, _ ->
                val newSongs = mutableListOf<Song>()
                value?.documents?.forEach { documentSnapshot ->
                    val song = documentSnapshot.toObject(Song::class.java) ?: return@forEach
                    newSongs.add(song)
                }
                _newSongs.value = newSongs
            }
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

    private fun getAllSection() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        fireBaseFireStore.collection("section")
            .addSnapshotListener { value, _ ->
                val allSection = mutableListOf<Section>()
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
                        val section = Section(id, name, songs)
                        allSection.add(section)
                    }
                }
                Tasks.whenAllComplete(tasks).addOnCompleteListener {
                    _allSection.value = allSection
                }
            }
    }

}