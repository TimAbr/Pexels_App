package com.example.pexelsapp.data.datasources.bookmarks.local

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.create(context)
    }

    @Provides
    fun providePhotosDao(database: AppDatabase): PhotosDao {
        return database.photosDao()
    }

    @Provides
    fun provideBookmarksDao(database: AppDatabase): BookmarksDao {
        return database.bookmarksDao()
    }

    @Provides
    fun provideCuratedCacheDao(database: AppDatabase): CuratedCacheDao {
        return database.curatedCacheDao()
    }
}