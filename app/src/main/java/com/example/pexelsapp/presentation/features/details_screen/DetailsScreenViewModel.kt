package com.example.pexelsapp.presentation.features.details_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError
import com.example.pexelsapp.domain.features.bookmarks.usecases.AddBookmarkUseCase
import com.example.pexelsapp.domain.features.bookmarks.usecases.DeleteBookmarkUseCase
import com.example.pexelsapp.domain.features.bookmarks.usecases.GetIsBookmarkedStateUseCase
import com.example.pexelsapp.domain.features.details.usecases.GetPhotoDetailsUseCase
import com.example.pexelsapp.domain.features.download_image.usecases.DownloadPhotoUseCase
import com.example.pexelsapp.presentation.navigation.RootGraph
import com.example.pexelsapp.utils.models.Outcome
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class DetailsScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPhotoDetailsUseCase: GetPhotoDetailsUseCase,
    private val getIsBookmarkedStateUseCase: GetIsBookmarkedStateUseCase,
    private val addBookmarkUseCase: AddBookmarkUseCase,
    private val deleteBookmarkUseCase: DeleteBookmarkUseCase,
    private val downloadPhotoUseCase: DownloadPhotoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.None)
    val uiState = _uiState.asStateFlow()

    private val photoId: Long = savedStateHandle.toRoute<RootGraph.Details>().photoId

    private var isBookmarkedLocal: Boolean = false

    init {
        observeBookmarkState()
        loadPhotoDetails()
    }

    fun tryAgain() {
        loadPhotoDetails()
    }


    private val bookmarkMutex = Mutex()

    fun onBookmarkClick() {
        val currentState = _uiState.value as? DetailsUiState.Content ?: return

        viewModelScope.launch {
            bookmarkMutex.withLock {
                if (currentState.isBookmarked) {
                    deleteBookmarkUseCase(currentState.photo.id)
                } else {
                    addBookmarkUseCase(currentState.photo)
                }
            }
        }
    }

    fun onDownloadClick() {
        val currentState = _uiState.value
        if (currentState is DetailsUiState.Content) {
            downloadPhotoUseCase(currentState.photo)
        }
    }

    private fun loadPhotoDetails() {
        _uiState.value = DetailsUiState.Loading

        viewModelScope.launch {
            when (val outcome = getPhotoDetailsUseCase(photoId)) {
                is Outcome.Success -> {
                    _uiState.value = DetailsUiState.Content(
                        photo = outcome.value,
                        isBookmarked = isBookmarkedLocal
                    )
                }
                is Outcome.Error -> {
                    if (outcome.type == PhotosRepositoryError.NOT_FOUND) {
                        _uiState.value = DetailsUiState.NotFound
                    } else {
                        _uiState.value = DetailsUiState.Error(outcome.message ?: "Unknown error")
                    }
                }
            }
        }
    }

    private fun observeBookmarkState() {
        viewModelScope.launch {
            getIsBookmarkedStateUseCase(photoId).collect { isBookmarked ->
                isBookmarkedLocal = isBookmarked

                val currentState = _uiState.value
                if (currentState is DetailsUiState.Content) {
                    if (currentState.isBookmarked != isBookmarked) {
                        _uiState.value = currentState.copy(isBookmarked = isBookmarked)
                    }
                }
            }
        }
    }
}

sealed class DetailsUiState {
    object None : DetailsUiState()
    object Loading : DetailsUiState()
    object NotFound : DetailsUiState()
    data class Error(val message: String) : DetailsUiState()
    data class Content(
        val photo: Photo,
        val isBookmarked: Boolean,
        val actionError: String? = null
    ) : DetailsUiState()
}