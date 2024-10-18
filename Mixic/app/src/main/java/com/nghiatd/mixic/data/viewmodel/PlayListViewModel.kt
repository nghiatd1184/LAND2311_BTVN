package com.nghiatd.mixic.data.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nghiatd.mixic.R
import com.nghiatd.mixic.data.model.Category
import com.nghiatd.mixic.data.model.Playlist
import com.nghiatd.mixic.data.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayListViewModel(context: Context) : ViewModel() {

    private val _devicePlaylists = MutableStateFlow<List<Playlist>>(emptyList())
    val devicePlaylists = _devicePlaylists.asStateFlow()

    private val _userPlaylist = MutableStateFlow<List<Playlist>>(emptyList())
    val userPlaylist = _userPlaylist.asStateFlow()


    private val sharedPref = context.getSharedPreferences("AppData", Context.MODE_PRIVATE)

    init {
        _devicePlaylists.value = getAllDevicePlaylists()
        getUserPlaylists()
    }

    fun getUserPlaylists() {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userDocRef = fireBaseFireStore.collection("users").document(userId)
            userDocRef.addSnapshotListener { documentSnapshot, exception ->
                if (exception != null) {
                    Log.e("PlayListViewModel", "Error getting user playlists", exception)
                    return@addSnapshotListener
                }
                if (documentSnapshot != null && documentSnapshot.exists()) {
                    val playlistRefs = documentSnapshot.get("playlists") as? List<*>
                    val playlists = mutableListOf<Playlist>()
                    val tasks = mutableListOf<Task<DocumentSnapshot>>()
                    playlistRefs?.forEach { playlistRef ->
                        if (playlistRef is DocumentReference) {
                            val task = playlistRef.get().addOnSuccessListener { playlistSnapshot ->
                                val id = playlistSnapshot.getString("id") ?: return@addOnSuccessListener
                                val name = playlistSnapshot.getString("name") ?: return@addOnSuccessListener
                                val songsRefs = playlistSnapshot.get("songs") as? List<DocumentReference>
                                val songs = mutableListOf<Song>()
                                songsRefs?.forEach { songRef ->
                                    val songTask = songRef.get().addOnSuccessListener { songSnapshot ->
                                        val song = songSnapshot.toObject(Song::class.java) ?: return@addOnSuccessListener
                                        songs.add(song)
                                    }
                                    tasks.add(songTask)
                                }
                                val playlist = Playlist(id, name, songs)
                                playlists.add(playlist)
                            }
                            tasks.add(task)
                        } else {
                            Log.e("PlayListViewModel", "Invalid playlist reference type")
                        }
                    }
                    Tasks.whenAllComplete(tasks).addOnCompleteListener {
                        _userPlaylist.value = playlists
                    }
                }
            }
        }
    }

    fun addNewPlaylistToFirebase(context: Context, playlistName: String, song: Song) {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        currentUser?.let { user ->
            val userId = user.uid
            val userDocRef = fireBaseFireStore.collection("users").document(userId)
            val newPlaylistRef = fireBaseFireStore.collection("playlist").document()
            val songRef = fireBaseFireStore.document("song/${song.id}")

            val newPlaylist = mapOf(
                "id" to newPlaylistRef.id,
                "name" to playlistName,
                "songs" to listOf(songRef)
            )

            newPlaylistRef.set(newPlaylist).addOnSuccessListener {
                userDocRef.update("playlists", FieldValue.arrayUnion(newPlaylistRef))
                    .addOnSuccessListener {
                        Toast.makeText(context, "Playlist \"$playlistName\" created and added to user", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PlayListViewModel", "Error adding playlist reference to user", e)
                        Toast.makeText(context, "Failed to add playlist to user", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                Log.e("PlayListViewModel", "Error creating new playlist", e)
                Toast.makeText(context, "Failed to create new playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun addSongToFirebasePlaylist(context: Context, playlist: Playlist, song: Song) {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        val playlistRef = fireBaseFireStore.collection("playlist").document(playlist.id)
        playlistRef.update("songs", FieldValue.arrayUnion(fireBaseFireStore.document("/song/${song.id}"))).addOnSuccessListener {

            Toast.makeText(context, "Song \"${song.name}\" added to playlist \"${playlist.name}\"", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            Log.e("PlayListViewModel", "Error adding song to playlist", e)
            Toast.makeText(context, "Failed to add song to playlist", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteFirebasePlaylist(context: Context, playlist: Playlist) {
        val fireBaseFireStore = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let { user ->
            val userId = user.uid
            val userDocRef = fireBaseFireStore.collection("users").document(userId)
            val playlistRef = fireBaseFireStore.collection("playlist").document(playlist.id)
            playlistRef.delete().addOnSuccessListener {
                userDocRef.update("playlists", FieldValue.arrayRemove(playlistRef))
                    .addOnSuccessListener {
                        Toast.makeText(context, "Playlist \"${playlist.name}\" deleted", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.e("PlayListViewModel", "Error removing playlist reference from user", e)
                        Toast.makeText(context, "Failed to remove playlist from user", Toast.LENGTH_SHORT).show()
                    }
            }.addOnFailureListener { e ->
                Log.e("PlayListViewModel", "Error deleting playlist", e)
                Toast.makeText(context, "Failed to delete playlist", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getAllDevicePlaylists(): List<Playlist> {
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

    fun addSongToPlaylist(context: Context, playlist: Playlist?, name: String, song: Song) {
        val playlists = devicePlaylists.value.toMutableList()
        if (playlist == null) {
            playlists.add(Playlist(System.currentTimeMillis().toString(),name, mutableListOf(song)))
        } else {
            val index = playlists.indexOf(playlist)
            if (playlists[index].songs.find { it.id == song.id } == null) {
                playlists[index].songs.add(song)
                Toast.makeText(context, "Song \"${song.name}\" added to playlist \"${playlist.name}\"", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, context.getString(R.string.song_already_added), Toast.LENGTH_SHORT).show()
                return
            }
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