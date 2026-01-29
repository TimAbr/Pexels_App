package com.example.pexelsapp.domain.features.bookmarks.usecases

import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import javax.inject.Inject

class GetBookmarksEvents@Inject constructor(
    private val bookmarksRepository: BookmarksRepository
) {
    operator fun invoke() = bookmarksRepository.bookmarksEvents
}