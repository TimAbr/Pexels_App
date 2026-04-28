package com.example.pexelsapp.presentation.features.main_screen.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.bookmarks.models.BookmarksEvent
import com.example.pexelsapp.domain.features.bookmarks.repositories.BookmarksRepositoryError
import com.example.pexelsapp.domain.features.bookmarks.usecases.GetBookmarksEvents
import com.example.pexelsapp.domain.features.bookmarks.usecases.GetBookmarksUseCase
import com.example.pexelsapp.domain.features.bookmarks.search.usecases.SearchBookmarksUseCase
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class BookmarksScreenViewModel @Inject constructor(
    private val getBookmarksUseCase: GetBookmarksUseCase,
    private val getBookmarksEvents: GetBookmarksEvents,
    private val searchBookmarksUseCase: SearchBookmarksUseCase,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _paginatedPhotos = MutableStateFlow<List<Photo>>(emptyList())
    private val _isPaginationLoading = MutableStateFlow(false)
    private val _paginationError = MutableStateFlow<BookmarksRepositoryError?>(null)

    val uiState = _searchQuery
        .debounce(400L)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isBlank()) {
                combine(
                    _paginatedPhotos,
                    _isPaginationLoading,
                    _paginationError
                ) { paginated, isPagLoading, pagError ->
                    when {
                        paginated.isEmpty() && isPagLoading -> BookmarksUiState.Loading
                        paginated.isEmpty() && pagError != null -> BookmarksUiState.Error(pagError)
                        paginated.isEmpty() -> BookmarksUiState.Empty
                        else -> BookmarksUiState.Content(
                            photos = paginated,
                            isPaginationLoading = isPagLoading,
                            error = pagError
                        )
                    }
                }
            } else {
                flow {
                    emit(BookmarksUiState.Loading)
                    val startTime = System.currentTimeMillis()
                    
                    val results = searchBookmarksUseCase(query)
                    
                    val elapsedTime = System.currentTimeMillis() - startTime
                    if (elapsedTime < MIN_SHIMMER_DURATION) {
                        delay(MIN_SHIMMER_DURATION - elapsedTime)
                    }

                    if (results.isEmpty()) {
                        emit(BookmarksUiState.Empty)
                    } else {
                        emit(BookmarksUiState.Content(photos = results))
                    }
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = BookmarksUiState.Loading
        )

    private var currentJob: Job? = null
    private var currentPage = 1
    private var isLastPage = false

    init {
        observeBookmarksEvents()
        loadInitialBookmarks()
    }

    private fun loadInitialBookmarks() {
        resetPagination()
        _paginatedPhotos.value = emptyList()
        executeLoad()
    }

    fun loadNextPage() {
        if (_searchQuery.value.isNotBlank() || _isPaginationLoading.value || isLastPage) return

        currentPage++
        executeLoad()
    }

    fun retry() {
        if (_searchQuery.value.isBlank()) {
            executeLoad()
        }
    }

    private fun executeLoad() {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            _isPaginationLoading.value = true
            _paginationError.value = null

            when (val outcome = getBookmarksUseCase(page = currentPage, perPage = BOOKMARKS_PER_PAGE)) {
                is Outcome.Success -> {
                    val newPhotos = outcome.value
                    isLastPage = newPhotos.size < BOOKMARKS_PER_PAGE
                    _paginatedPhotos.value = _paginatedPhotos.value + newPhotos
                }
                is Outcome.Error -> {
                    _paginationError.value = outcome.type
                }
            }
            _isPaginationLoading.value = false
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    private fun observeBookmarksEvents() {
        viewModelScope.launch {
            getBookmarksEvents().collect { event ->
                when (event) {
                    is BookmarksEvent.Added -> {
                        if (_paginatedPhotos.value.none { it.id == event.photo.id }) {
                            _paginatedPhotos.value = listOf(event.photo) + _paginatedPhotos.value
                        }
                    }
                    is BookmarksEvent.Deleted -> {
                        _paginatedPhotos.value = _paginatedPhotos.value.filterNot { it.id == event.photoId }
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
        private const val MIN_SHIMMER_DURATION = 500L
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
