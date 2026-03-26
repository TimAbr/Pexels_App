package com.example.pexelsapp.presentation.features.main_screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoGroupType
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.domain.features.home.usecases.GetCachedPhotosUseCase
import com.example.pexelsapp.domain.features.home.usecases.GetPhotosUseCase
import com.example.pexelsapp.domain.features.home.usecases.GetCategoriesUseCase
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getPhotosUseCase: GetPhotosUseCase,
    private val getCachedPhotosUseCase: GetCachedPhotosUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.None)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val categories = when (val res = getCategoriesUseCase()) {
        is Outcome.Success -> res.value
        is Outcome.Error -> emptyList()
    }

    private val _selectedCategory = MutableStateFlow<PhotoGroupType>(PhotoGroupType.Curated)
    val selectedCategory = _selectedCategory.asStateFlow()

    private fun getActiveGroupType(): PhotoGroupType {
        val query = _searchQuery.value
        return if (query.isNotBlank()) PhotoGroupType.Query(query) else _selectedCategory.value
    }

    private var currentJob: Job? = null
    private var currentPage = 1
    private var isLastPage = false
    private var isNetworkFirstPageLoaded = false
    private var isCacheLoaded = false

    init {
        loadPhotos()
    }


    init {
        observeSearchQuery()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(600L)
                .collectLatest { query ->
                    resetPagination()
                    _uiState.value = HomeUiState.Loading
                    loadPhotos()
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState !is HomeUiState.Content || currentState.isPaginationLoading || isLastPage || currentState.error != null)
            return

        if (!isNetworkFirstPageLoaded) {
            return
        }

        currentPage++
        loadPhotos()
    }

    fun retry() {
        loadPhotos()
    }

    private fun loadPhotos() {
        val activeType = getActiveGroupType()

        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            if (currentPage == 1) {
                loadFirstPage(activeType)
            } else {
                loadSubsequentPages(activeType)
            }
        }
    }

    private suspend fun loadFirstPage(activeType: PhotoGroupType) {
        val cached = getCachedPhotosUseCase(activeType)
        if (cached is Outcome.Success && cached.value.photos.isNotEmpty()) {
            _uiState.value = HomeUiState.Content(
                photos = cached.value.photos,
                isPaginationLoading = true,
                error = null
            )
        } else {
            _uiState.value = HomeUiState.Loading
        }

        val outcome = getPhotosUseCase(type = activeType, page = currentPage, perPage = PHOTOS_PER_PAGE)
        when (outcome) {
            is Outcome.Success -> {
                val newPhotos = outcome.value.photos
                isLastPage = newPhotos.size < PHOTOS_PER_PAGE
                isNetworkFirstPageLoaded = true
                _uiState.value = if (newPhotos.isEmpty()) HomeUiState.Empty else HomeUiState.Content(photos = newPhotos)
            }
            is Outcome.Error -> handleLoadError(outcome.type)
        }
    }

    private suspend fun loadSubsequentPages(activeType: PhotoGroupType) {
        val currentState = _uiState.value as? HomeUiState.Content
        if (currentState != null) {
            _uiState.value = currentState.copy(isPaginationLoading = true, error = null)
        }

        val outcome = getPhotosUseCase(type = activeType, page = currentPage, perPage = PHOTOS_PER_PAGE)
        when (outcome) {
            is Outcome.Success -> {
                val newPhotos = outcome.value.photos
                isLastPage = newPhotos.size < PHOTOS_PER_PAGE
                val currentContent = _uiState.value as? HomeUiState.Content
                if (currentContent != null) {
                    _uiState.value = currentContent.copy(
                        photos = currentContent.photos + newPhotos,
                        isPaginationLoading = false,
                        error = null
                    )
                } else {
                    _uiState.value = if (newPhotos.isEmpty()) HomeUiState.Empty else HomeUiState.Content(photos = newPhotos)
                }
            }
            is Outcome.Error -> handleLoadError(outcome.type)
        }
    }

    private fun handleLoadError(error: PhotosRepositoryError?) {
        val currentContent = _uiState.value as? HomeUiState.Content
        if (currentContent != null) {
            if (currentContent.photos.isEmpty()){
                _uiState.value = HomeUiState.Error(error)
            } else {
                _uiState.value = currentContent.copy(
                    isPaginationLoading = false,
                    error = error
                )
            }
        } else {
            _uiState.value = HomeUiState.Error(error)
        }
    }

    private fun resetPagination() {
        currentPage = 1
        currentJob?.cancel()
        isLastPage = false
        isNetworkFirstPageLoaded = false
    }

    fun selectCategoryByIndex(index: Int) {
        if (index !in categories.indices) return
        val newSelection = PhotoGroupType.Category(categories[index].name)
        val oldQuery = _searchQuery.value
        
        if (oldQuery.isEmpty() && _selectedCategory.value != newSelection) {
            _selectedCategory.value = newSelection
            resetPagination()
            _uiState.value = HomeUiState.Loading
            loadPhotos()
        } else {
            _selectedCategory.value = newSelection
            _searchQuery.value = ""
        }
    }

    fun selectCurated() {
        val oldQuery = _searchQuery.value
        
        if (oldQuery.isEmpty() && _selectedCategory.value !is PhotoGroupType.Curated) {
            _selectedCategory.value = PhotoGroupType.Curated
            resetPagination()
            _uiState.value = HomeUiState.Loading
            loadPhotos()
        } else {
            _selectedCategory.value = PhotoGroupType.Curated
            _searchQuery.value = ""
        }
    }

    companion object {
        private const val PHOTOS_PER_PAGE = 30
    }
}

sealed class HomeUiState {
    object None : HomeUiState()
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Error(val error: PhotosRepositoryError?) : HomeUiState()
    data class Content(
        val photos: List<Photo>,
        val isPaginationLoading: Boolean = false,
        val error: PhotosRepositoryError? = null
    ) : HomeUiState()
}
