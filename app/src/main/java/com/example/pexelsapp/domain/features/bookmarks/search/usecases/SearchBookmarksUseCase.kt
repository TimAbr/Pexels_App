package com.example.pexelsapp.domain.features.bookmarks.search.usecases

import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepository
import com.example.pexelsapp.domain.features.bookmarks.search.models.SearchMatcher
import com.example.pexelsapp.utils.models.Outcome
import javax.inject.Inject
import kotlin.math.max

class SearchBookmarksUseCase @Inject constructor(
    private val bookmarksRepository: BookmarksRepository,
    private val searchMatcher: SearchMatcher
) {
    suspend operator fun invoke(query: String): List<Photo> {
        if (query.isBlank()) return emptyList()

        val allBookmarks = mutableListOf<Photo>()
        var currentPage = 1
        val perPage = BookmarksRepository.DEFAULT_BOOKMARKS_BY_PAGE

        while (true) {
            val outcome = bookmarksRepository.getBookmarksPage(currentPage, perPage)
            if (outcome is Outcome.Success) {
                val photos = outcome.value
                allBookmarks.addAll(photos)
                if (photos.size < perPage) break
                currentPage++
            } else {
                break
            }
        }

        return allBookmarks
            .map { photo ->
                val descriptionScore = searchMatcher.match(query, photo.description)
                val authorNameScore = searchMatcher.match(query, photo.photographer.name)
                val score = max(descriptionScore, authorNameScore)
                photo to score
            }
            .filter { it.second > 0.0 }
            .sortedByDescending { it.second }
            .map { it.first }
    }
}