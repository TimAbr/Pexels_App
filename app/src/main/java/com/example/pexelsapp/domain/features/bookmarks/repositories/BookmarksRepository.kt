package com.example.pexelsapp.domain.features.bookmarks.repositories

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepository.Companion.DEFAULT_PHOTOS_BY_PAGE
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.utils.models.Outcome

interface BookmarksRepository {
    fun addPhoto(
        photo: Photo
    ): Outcome<Unit, BookmarkRepositoryError>

    fun getPhoto(
        photoId: Int
    ): Outcome<Photo, BookmarkRepositoryError>

    fun getBookmarks(
        page: Int,
        perPage: Int = DEFAULT_PHOTOS_BY_PAGE
    ): Outcome<List<Photo>, PhotosRepositoryError>
}