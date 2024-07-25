package vn.study.qlchsach

import android.content.Context
import androidx.room.Database
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase
import vn.study.qlchsach.dao.PublisherDao
import vn.study.qlchsach.entity.Publisher

@Database(entities = [Publisher::class], version = 1, exportSchema = false)
abstract class QLDatabase : RoomDatabase() {

    companion object {

        // static in Java

        private var instance: QLDatabase? = null

        @Synchronized
        fun getInstance(context: Context): QLDatabase {
            if (instance == null) {
                instance = databaseBuilder(
                    context.applicationContext,
                    QLDatabase::class.java,
                    "qlchs.db"
                )
                    .allowMainThreadQueries()
                    .addMigrations()
                    .build()
            }
            return instance!!
        }
    }

    abstract fun getPublisherDao(): PublisherDao
}