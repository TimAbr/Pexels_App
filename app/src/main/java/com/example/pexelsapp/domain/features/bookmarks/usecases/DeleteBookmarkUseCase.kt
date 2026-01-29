package com.example.pexelsapp.domain.features.bookmarks.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import javax.inject.Inject

class DeleteBookmarkUseCase  @Inject constructor(
    private val bookmarksRepository: BookmarksRepository
) {
    suspend operator fun invoke(photoId: Long) =
        bookmarksRepository.deletePhoto(photoId)
}