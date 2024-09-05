package com.phucdv.musicdemo

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Suppress("DEPRECATION")
class MusicRepository(private val context: Context) {

    suspend fun getAllSongs() = withContext(Dispatchers.IO) {
        val listSongs = ArrayList<Song>()

        try {
            val contentResolver = context.contentResolver

            val fields = arrayOf(
                Media._ID,
                Media.TITLE,
                Media.ARTIST,
                Media.ALBUM_ID
            )
            contentResolver.query(
                Media.EXTERNAL_CONTENT_URI, fields, null,
                null, "${Media.TITLE_KEY} ASC"
            )?.let { cursor ->
                val idIndex = cursor.getColumnIndexOrThrow(Media._ID)
                val titleIndex = cursor.getColumnIndexOrThrow(Media.TITLE)
                val artistIndex = cursor.getColumnIndexOrThrow(Media.ARTIST)
                val albumIdIndex = cursor.getColumnIndexOrThrow(Media.ALBUM_ID)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idIndex)
                    val title = cursor.getString(titleIndex)
                    val artist = cursor.getString(artistIndex)
                    val albumId = cursor.getLong(albumIdIndex)
                    val artUri = Uri.parse("content://media/external/audio/$albumId")

                    val song = Song(id, title, artist, artUri)
                    listSongs.add(song)
                }

                cursor.close()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        listSongs
    }

}