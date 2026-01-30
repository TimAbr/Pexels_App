package com.example.pexelsapp.presentation.features.main_screen.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.presentation.features.components.ScreenStub
import com.example.pexelsapp.presentation.features.components.PhotoGrid
import com.example.pexelsapp.presentation.features.components.PhotoGridShimmer
import com.example.pexelsapp.presentation.features.main_screen.home.RetrySection

@Composable
fun BookmarksScreen(
    modifier: Modifier = Modifier,
    viewModel: BookmarksScreenViewModel = hiltViewModel(),
    onPhotoClick: (Photo) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState is BookmarksUiState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            when (val state = uiState) {

                is BookmarksUiState.Content -> {
                    PhotoGrid(
                        photos = state.photos,
                        isPaginationLoading = state.isPaginationLoading,
                        onPhotoClick = onPhotoClick,
                        onLoadMore = viewModel::loadNextPage,
                        error = state.error,
                        errorContent = {
                            RetrySection(onRetry = viewModel::retry)
                        },
                        showAuthorNames = true
                    )

                }

                is BookmarksUiState.Empty -> {
                    ScreenStub(
                        text = stringResource(R.string.no_bookmarks),
                        buttonText = stringResource(R.string.try_again),
                        onButtonClick = viewModel::retry
                    )
                }

                is BookmarksUiState.Error -> {
                    ScreenStub(
                        text = stringResource(R.string.bookmarks_error),
                        buttonText = stringResource(R.string.try_again),
                        onButtonClick = viewModel::retry
                    )
                }

                is BookmarksUiState.Loading -> {
                    PhotoGridShimmer()
                }

                is BookmarksUiState.None -> Unit
            }
        }
    }
}