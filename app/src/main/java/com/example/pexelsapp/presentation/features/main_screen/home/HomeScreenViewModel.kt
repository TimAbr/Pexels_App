package com.example.pexelsapp.presentation.features.main_screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.domain.features.home.models.Category
import com.example.pexelsapp.domain.features.home.usecases.GetCategoriesUseCase
import com.example.pexelsapp.domain.features.home.usecases.GetCuratedPhotosUseCase
import com.example.pexelsapp.domain.features.home.usecases.GetPhotosByCategoryUseCase
import com.example.pexelsapp.domain.features.home.usecases.GetPhotosByQueryUseCase
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val getCuratedPhotosUseCase: GetCuratedPhotosUseCase,
    private val getPhotosByQueryUseCase: GetPhotosByQueryUseCase,
    private val getPhotosByCategoryUseCase: GetPhotosByCategoryUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.None)
    val uiState = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val categories = when (val res = getCategoriesUseCase()) {
        is Outcome.Success -> res.value
        is Outcome.Error -> emptyList()
    }

    private val _selectedCategory =
        MutableStateFlow<SelectedCategory>(SelectedCategory.Curated)
    val selectedCategory = _selectedCategory.asStateFlow()

    private var currentJob: Job? = null

    private var currentPage = 1
    private var isLastPage = false

    init {
        observeSearchQuery()
        resetPagination()
        loadPhotosForSelectedCategory()
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(600L)
                .collectLatest { query ->
                    resetPagination()
                    if (query.isBlank()) {
                        loadPhotosForSelectedCategory()
                    } else {
                        searchPhotos(query)
                    }
                }
        }
    }

    fun onQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState !is HomeUiState.Content
            || currentState.isPaginationLoading || isLastPage)
            return

        currentPage++
        executeLoad()
    }

    fun loadPage(){
        _uiState.value = HomeUiState.Loading
        executeLoad()
    }

    private fun executeLoad(){
        val query = _searchQuery.value

        if (query.isBlank()) {
            loadPhotosForSelectedCategory()
        } else {
            searchPhotos(query)
        }
    }

    private fun loadCuratedPhotos() {
        executePhotoRequest {
            getCuratedPhotosUseCase(
                page = currentPage,
                perPage = PHOTOS_PER_PAGE)
        }
    }

    private fun loadPhotosByCategory(category: Category) {
        executePhotoRequest  {
            getPhotosByCategoryUseCase(
                category = category,
                page = currentPage,
                perPage = PHOTOS_PER_PAGE
            )
        }
    }

    private fun searchPhotos(query: String) {
        executePhotoRequest {
            getPhotosByQueryUseCase(
                query = query,
                page = currentPage,
                perPage = PHOTOS_PER_PAGE
            )
        }
    }

    private fun executePhotoRequest(
        flowProvider: () -> Flow<Outcome<List<Photo>, PhotosRepositoryError>>
    ) {
        currentJob?.cancel()
        currentJob = viewModelScope.launch {
            val currentState = _uiState.value

            if (currentState is HomeUiState.Content) {
                _uiState.value = currentState.copy(isPaginationLoading = true)
            } else {
                _uiState.value = HomeUiState.Loading
            }

            flowProvider().collect { outcome ->
                when (outcome) {
                    is Outcome.Success -> {
                        val newPhotos = outcome.value
                        isLastPage = newPhotos.size < PHOTOS_PER_PAGE

                        if (_uiState.value is HomeUiState.Content) {
                            val prev = _uiState.value as HomeUiState.Content
                            _uiState.value = prev.copy(
                                photos = prev.photos + newPhotos,
                                isPaginationLoading = false
                            )
                        } else {
                            if (newPhotos.isEmpty()) {
                                _uiState.value = HomeUiState.Empty
                            } else {
                                _uiState.value = HomeUiState.Content(photos = newPhotos)
                            }
                        }
                    }
                    is Outcome.Error -> {
                        _uiState.value = HomeUiState.Error(outcome.type)
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

    private fun loadPhotosForSelectedCategory() {
        when (val sel = _selectedCategory.value) {
            is SelectedCategory.Curated -> loadCuratedPhotos()
            is SelectedCategory.Category -> {
                val idx = sel.id
                if (idx in categories.indices) {
                    loadPhotosByCategory(categories[idx])
                } else {
                    _selectedCategory.value = SelectedCategory.Curated
                    loadCuratedPhotos()
                }
            }
        }
    }

    fun selectCategoryByIndex(index: Int) {
        if (index !in categories.indices) return
        _selectedCategory.value = SelectedCategory.Category(index)
        resetPagination()
        if (_searchQuery.value.isBlank()) {
            loadPhotosForSelectedCategory()
        }
    }

    fun selectCurated() {
        _selectedCategory.value = SelectedCategory.Curated
        resetPagination()
        if (_searchQuery.value.isBlank()) {
            loadCuratedPhotos()
        }
    }

    companion object {
        private const val PHOTOS_PER_PAGE = 30
    }
}

sealed class SelectedCategory {
    object Curated : SelectedCategory()
    data class Category(val id: Int) : SelectedCategory()
}

sealed class HomeUiState {
    object None : HomeUiState()
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Error(val error: PhotosRepositoryError?) : HomeUiState()
    data class Content(
        val photos: List<Photo>,
        val isPaginationLoading: Boolean = false
    ) : HomeUiState()
}