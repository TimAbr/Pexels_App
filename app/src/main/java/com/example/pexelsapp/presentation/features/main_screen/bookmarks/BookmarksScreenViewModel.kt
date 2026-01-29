package com.example.pexelsapp.presentation.features.main_screen.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.models.BookmarksEvent
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepositoryError
import com.example.pexelsapp.domain.features.bookmarks.usecases.GetBookmarksEvents
import com.example.pexelsapp.domain.features.bookmarks.usecases.GetBookmarksUseCase
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksScreenViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val getBookmarksEvents: GetBookmarksEvents
) : ViewModel() {

    private val _uiState = MutableStateFlow<BookmarksUiState>(BookmarksUiState.None)
    val uiState = _uiState.asStateFlow()

    private var currentJob: Job? = null
    private var currentPage = 1
    private var isLastPage = false

    init {
        observeBookmarksEvents()
        loadInitialBookmarks()
    }

    private fun loadInitialBookmarks() {
        resetPagination()
        _uiState.value = BookmarksUiState.Loading
        executeLoad()
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState !is BookmarksUiState.Content ||
            currentState.isPaginationLoading || isLastPage
        ) return

        currentPage++
        executeLoad()
    }

    fun retry() {
        executeLoad()
    }

    private fun executeLoad() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState is BookmarksUiState.Content) {
                _uiState.value = currentState.copy(isPaginationLoading = true, error = null)
            } else if (currentState !is BookmarksUiState.Loading) {
                _uiState.value = BookmarksUiState.Loading
            }

            when (val outcome = getBookmarksUseCase(page = currentPage, perPage = BOOKMARKS_PER_PAGE)) {
                is Outcome.Success -> {
                    val newPhotos = outcome.value
                    isLastPage = newPhotos.size < BOOKMARKS_PER_PAGE

                    val currentContent = _uiState.value as? BookmarksUiState.Content
                    if (currentContent != null && currentPage > 1) {
                        _uiState.value = currentContent.copy(
                            photos = currentContent.photos + newPhotos,
                            isPaginationLoading = false,
                            error = null
                        )
                    } else {
                        if (newPhotos.isEmpty()) {
                            _uiState.value = BookmarksUiState.Empty
                        } else {
                            _uiState.value = BookmarksUiState.Content(photos = newPhotos)
                        }
                    }
                }
                is Outcome.Error -> {
                    val currentContent = _uiState.value as? BookmarksUiState.Content
                    if (currentContent != null) {
                        _uiState.value = currentContent.copy(
                            isPaginationLoading = false,
                            error = outcome.type
                        )
                    } else {
                        _uiState.value = BookmarksUiState.Error(outcome.type)
                    }
                }
            }
        }
    }

    private fun observeBookmarksEvents() {
        viewModelScope.launch {
            getBookmarksEvents().collect { event ->
                val currentState = _uiState.value
                when (event) {
                    is BookmarksEvent.Added -> {
                        if (currentState is BookmarksUiState.Content) {
                            if (currentState.photos.none { it.id == event.photo.id }) {
                                _uiState.value = currentState.copy(
                                    photos = listOf(event.photo) + currentState.photos
                                )
                            }
                        } else if (currentState is BookmarksUiState.Empty || currentState is BookmarksUiState.None) {
                            _uiState.value = BookmarksUiState.Content(photos = listOf(event.photo))
                        }
                    }
                    is BookmarksEvent.Deleted -> {
                        if (currentState is BookmarksUiState.Content) {
                            val updatedList = currentState.photos.filterNot { it.id == event.photoId }
                            if (updatedList.isEmpty()) {
                                _uiState.value = BookmarksUiState.Empty
                            } else {
                                _uiState.value = currentState.copy(photos = updatedList)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun resetPagination() {
        currentPage = 1
        currentJob?.cancel()
        isLastPage = false
    }

    companion object {
        private const val BOOKMARKS_PER_PAGE = 30
    }
}

sealed class BookmarksUiState {
    object None : BookmarksUiState()
    object Loading : BookmarksUiState()
    object Empty : BookmarksUiState()
    data class Error(val error: BookmarksRepositoryError?) : BookmarksUiState()
    data class Content(
        val photos: List<Photo>,
        val isPaginationLoading: Boolean = false,
        val error: BookmarksRepositoryError? = null
    ) : BookmarksUiState()
}