package com.example.pexelsapp.domain.features.bookmarks.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import javax.inject.Inject

class AddBookmarkUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository
) {
    suspend operator fun invoke(photo: Photo) =
        bookmarksRepository.savePhoto(photo)
}