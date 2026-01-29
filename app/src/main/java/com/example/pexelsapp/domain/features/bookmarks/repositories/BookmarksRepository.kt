package com.example.pexelsapp.domain.features.bookmarks.repositories

import com.example.pexelsapp.domain.common.models.Photo

import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.Flow

interface BookmarksRepository {

    fun getAllBookmarks(): Flow<List<Photo>>

    suspend fun getBookmarksPage(
        page: Int,
        perPage: Int = DEFAULT_BOOKMARKS_BY_PAGE
    ): Outcome<List<Photo>, BookmarksRepositoryError>

    suspend fun savePhoto(photo: Photo): Outcome<Unit, BookmarksRepositoryError>

    suspend fun deletePhoto(photoId: Long): Outcome<Unit, BookmarksRepositoryError>

    fun observeIsBookmarked(photoId: Long): Flow<Boolean>

    suspend fun isBookmarked(photoId: Long): Boolean

    companion object {
        const val DEFAULT_BOOKMARKS_BY_PAGE = 30
    }
}