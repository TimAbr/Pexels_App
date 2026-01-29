package com.example.pexelsapp.domain.features.bookmarks.usecases

import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarksUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository
) {
    suspend operator fun invoke(
        page: Int,
        perPage: Int = BookmarksRepository.DEFAULT_BOOKMARKS_BY_PAGE
    ) = bookmarksRepository.getBookmarksPage(
        page = page,
        perPage = perPage
    )
}