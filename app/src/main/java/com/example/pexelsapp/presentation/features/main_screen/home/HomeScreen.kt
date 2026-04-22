package com.example.pexelsapp.presentation.features.main_screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.models.PhotoGroupType
import com.example.pexelsapp.domain.features.home.models.Category
import com.example.pexelsapp.presentation.features.components.PhotoGrid
import com.example.pexelsapp.presentation.features.components.PhotoGridShimmer
import com.example.pexelsapp.presentation.features.components.RetrySection
import com.example.pexelsapp.presentation.features.components.ScreenStub
import com.example.pexelsapp.presentation.features.components.SearchBar


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel = hiltViewModel(),
    onPhotoClick: (Photo) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = viewModel.categories

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        SearchBar(
            query = searchQuery,
            onQueryChange = viewModel::onQueryChange,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )

        HomeCategoryList(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategoryClick = { category ->
                val originalIdx = categories.indexOf(category)
                viewModel.selectCategoryByIndex(originalIdx)
            },
            onCuratedClick = viewModel::selectCurated
        )

        if (uiState is HomeUiState.Loading) {
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
                is HomeUiState.Loading -> {
                    PhotoGridShimmer()
                }
                is HomeUiState.Content -> {
                    PhotoGrid(
                        photos = state.photos,
                        isPaginationLoading = state.isPaginationLoading,
                        onPhotoClick = onPhotoClick,
                        onLoadMore = viewModel::loadNextPage,
                        error = state.error,
                        errorContent = {
                            RetrySection(onRetry = viewModel::retry)
                        }
                    )
                }
                is HomeUiState.Empty -> {
                    ScreenStub(
                        text = stringResource(R.string.no_results_found),
                        buttonText = stringResource(R.string.explore),
                        onButtonClick = viewModel::retry
                    )
                }
                is HomeUiState.Error -> {
                    ScreenStub(
                        text = stringResource(R.string.network_error),
                        buttonText = stringResource(R.string.try_again),
                        onButtonClick = viewModel::retry,
                        iconRes = R.drawable.no_network_icon
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
fun HomeCategoryList(
    categories: List<Category>,
    selectedCategory: PhotoGroupType,
    onCategoryClick: (Category) -> Unit,
    onCuratedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val selectedCategoryName = (selectedCategory as? PhotoGroupType.Category)?.category

        if(selectedCategoryName != null){
            val chosenCategory = categories.find { it.name == selectedCategoryName }
            if (chosenCategory != null) {
                item {
                    CategoryChip(
                        label = chosenCategory.name,
                        isSelected = true,
                        onClick = { onCategoryClick(chosenCategory) }
                    )
                }
            }
        }

        item {
            CategoryChip(
                label = stringResource(R.string.curated),
                isSelected = selectedCategory is PhotoGroupType.Curated,
                onClick = onCuratedClick
            )
        }

        items(
            categories.filter { it.name != selectedCategoryName }
        ) { category ->
            CategoryChip(
                label = category.name,
                isSelected = false,
                onClick = { onCategoryClick(category) }
            )
        }
    }
}

@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(100.dp))
            .clickable { onClick() },
        color = if (isSelected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
