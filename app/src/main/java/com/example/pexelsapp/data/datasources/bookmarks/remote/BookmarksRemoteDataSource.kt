package com.example.pexelsapp.data.datasources.bookmarks.remote

import com.example.pexelsapp.data.models.BookmarkedPhotoDto
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow

interface BookmarksRemoteDataSource {
    val syncEvents: Flow<List<RemoteBookmarkSyncEvent>>
    
    suspend fun getBookmarks(
        userId: String,
    ): Outcome<List<BookmarkedPhotoDto>, Exception>

    suspend fun saveBookmark(
        userId: String,
        bookmark: BookmarkedPhotoDto,
    ): Outcome<Unit, Exception>

    suspend fun deleteBookmark(
        userId: String,
        photoId: Long,
    ): Outcome<Unit, Exception>

    suspend fun saveBookmarksBatch(
        userId: String,
        bookmarks: List<BookmarkedPhotoDto>,
    ): Outcome<Unit, Exception>
}
