package com.example.pexelsapp.domain.features.bookmarks.usecases

import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetIsBookmarkedStateUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository
) {
    operator fun invoke(photoId: Long): Flow<Boolean> =
        bookmarksRepository.observeIsBookmarked(photoId)
}