package com.nghiatd.quanlycuahangroom.handler

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nghiatd.quanlycuahangroom.dao.ProductDao
import com.nghiatd.quanlycuahangroom.entity.Product

@Database(entities = [Product::class], version = 1)
abstract class DatabaseHandler : RoomDatabase() {
    abstract fun ProductDao(): ProductDao

    companion object {
        private var instance: DatabaseHandler? = null
        fun getInstance(context: Context): DatabaseHandler {
            return instance ?: synchronized(this) {
                val newInstance = Room.databaseBuilder(
                    context.applicationContext,
                    DatabaseHandler::class.java,
                    "qlch_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                instance = newInstance
                return instance!!
            }
        }
    }
}