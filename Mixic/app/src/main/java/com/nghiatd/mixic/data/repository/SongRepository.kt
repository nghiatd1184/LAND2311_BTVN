package com.nghiatd.mixic.data.repository

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.google.firebase.Timestamp
import com.nghiatd.mixic.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.Normalizer

class SongRepository(val context: Context) {
    suspend fun getAllDeviceSongs() = withContext(Dispatchers.IO) {
        val listSongs = ArrayList<Song>()

        try {
            val contentResolver = context.contentResolver

            val projection = arrayOf(
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.DATA
            )
            val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
            val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

            contentResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val albumIdColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val dataColum = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColum)
                    val title = cursor.getString(titleColum)
                    val artist = cursor.getString(artistColum)
                    val albumId = cursor.getLong(albumIdColum)
                    val data = cursor.getString(dataColum)
                    val albumArtUri = getAlbumArtUri(albumId)
                    val time = Timestamp.now()
                    val song = Song(id.toString(), title, artist, albumArtUri.toString(), data, time)
                    listSongs.add(song)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        listSongs
    }

    suspend fun searchSongFromDevice(query: String) = withContext(Dispatchers.IO) {
        val listSongs = getAllDeviceSongs()
        val normalizedQuery = query.normalize()
        listSongs.filter {
            it.name.normalize().contains(normalizedQuery, ignoreCase = true) ||
                    it.artist.normalize().contains(normalizedQuery, ignoreCase = true)
        }
    }

    private fun String.normalize(): String {
        return Normalizer.normalize(this, Normalizer.Form.NFD)
            .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
    }

    private fun getAlbumArtUri(albumId: Long): Uri {
        val albumArtUri = Uri.parse("content://media/external/audio/albumart")
        return ContentUris.withAppendedId(albumArtUri, albumId)
    }
}