package com.example.pexelsapp.presentation.features.main_screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.home.models.Category
import com.example.pexelsapp.presentation.features.components.ScreenStub
import com.example.pexelsapp.presentation.features.components.PhotoGrid
import com.example.pexelsapp.presentation.features.components.PhotoGridShimmer


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

        HomeSearchBar(
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
fun HomeSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            stringResource(R.string.search_hint),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    innerTextField()
                }
                if (query.isNotEmpty()) {
                    IconButton(onClick = { onQueryChange("") }) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }
            }
        }
    )
}

@Composable
fun HomeCategoryList(
    categories: List<Category>,
    selectedCategory: SelectedCategory,
    onCategoryClick: (Category) -> Unit,
    onCuratedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryId: Int? = null

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if(selectedCategory is SelectedCategory.Category){
            selectedCategoryId = selectedCategory.id
            val chosenCategory = categories[selectedCategoryId]

            item{
                CategoryChip(
                    label = chosenCategory.name,
                    isSelected = true,
                    onClick = { onCategoryClick(chosenCategory) }
                )
            }
        }

        item {
            CategoryChip(
                label = stringResource(R.string.curated),
                isSelected = selectedCategoryId==null,
                onClick = onCuratedClick
            )
        }

        items(
            categories.filterIndexed { index, category -> index!= selectedCategoryId}
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



@Composable
fun RetrySection(
    onRetry: ()->Unit
){
    ScreenStub(
        text = stringResource(R.string.network_error),
        buttonText = stringResource(R.string.try_again),
        onButtonClick = onRetry
    )
}