package com.example.pexelsapp.data.datasources.bookmarks.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.pexelsapp.data.models.BookmarkDbo
import com.example.pexelsapp.data.models.CuratedCacheDbo
import com.example.pexelsapp.data.models.PhotoDbo

@Database(
    entities = [PhotoDbo::class, BookmarkDbo::class, CuratedCacheDbo::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photosDao(): PhotosDao
    abstract fun bookmarksDao(): BookmarksDao
    abstract fun curatedCacheDao(): CuratedCacheDao

    companion object {
        private const val DB_NAME = "pexels_database"
        fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                DB_NAME
            )
                .fallbackToDestructiveMigration(false)
                .build()
        }
    }
}