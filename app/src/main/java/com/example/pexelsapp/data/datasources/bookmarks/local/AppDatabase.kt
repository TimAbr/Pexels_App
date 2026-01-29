package com.example.pexelsapp.data.datasources.bookmarks.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pexelsapp.data.models.PhotoDbo

@Database(entities = [PhotoDbo::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): SavedPhotosDao

    companion object {
        private const val DB_NAME = "pexels_database"

        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            ).build()
        }
    }
}