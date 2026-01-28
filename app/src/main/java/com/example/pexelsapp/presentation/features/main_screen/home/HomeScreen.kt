package com.example.pexelsapp.presentation.features.main_screen.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.features.home.models.Category


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeScreenViewModel,
    onPhotoClick: (Int) -> Unit // Переход на детали [cite: 72]
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val categories = viewModel.categories // Список 7 категорий [cite: 47]

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


        val displayCategories = remember(selectedCategory, categories) {
            val curCategory = selectedCategory
            if (curCategory is SelectedCategory.Category)
                listOf(categories[curCategory.id]) +
                        categories.filterIndexed { index, category ->
                    index != curCategory.id
                }
            else
                categories
        }

        HomeCategoryList(
            categories = displayCategories,
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
                        onLoadMore = viewModel::loadNextPage
                    )
                }
                is HomeUiState.Empty -> {
                    HomeStub(
                        text = stringResource(R.string.no_results_found),
                        buttonText = stringResource(R.string.explore),
                        onButtonClick = viewModel::loadPage
                    )
                }
                is HomeUiState.Error -> {
                    HomeStub(
                        text = stringResource(R.string.network_error),
                        buttonText = stringResource(R.string.try_again),
                        onButtonClick = viewModel::loadPage
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
    TextField(
        value = query,
        onValueChange = onQueryChange,
        shape = CircleShape,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        placeholder = {
            Text(stringResource(R.string.search_hint))
                      },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary)
                      },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            }
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        singleLine = true
    )
}

@Composable
fun PhotoGrid(
    photos: List<Photo>,
    isPaginationLoading: Boolean,
    onPhotoClick: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyGridState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                if (lastIndex != null && lastIndex >= photos.size - 5) {
                    onLoadMore()
                }
            }
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(photos) { photo ->
            PhotoCard(
                photo = photo,
                onClick = { onPhotoClick(photo.id) }
            )
        }

        if (isPaginationLoading) {
            item(span = { GridItemSpan(2) }) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp).padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun PhotoCard(photo: Photo, onClick: () -> Unit) {
    var isImageLoaded by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isImageLoaded) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "photo_fade_in"
    )

    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {


        AsyncImage(
            model = photo.source.original,
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier.fillMaxWidth()
                .graphicsLayer(alpha = alpha),
            onSuccess = { isImageLoaded = true },
            placeholder = painterResource(R.drawable.placeholder)
        )
    }
}

@Composable
fun HomeCategoryList(
    categories: List<Category>,
    selectedCategory: SelectedCategory,
    onCategoryClick: (Category) -> Unit,
    onCuratedClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            CategoryChip(
                label = stringResource(R.string.curated),
                isSelected = selectedCategory is SelectedCategory.Curated,
                onClick = onCuratedClick
            )
        }

        items(categories) { category ->
            val isSelected = (selectedCategory as? SelectedCategory.Category)?.let {
                categories.getOrNull(it.id)?.name == category.name
            } ?: false

            CategoryChip(
                label = category.name,
                isSelected = isSelected,
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
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun HomeStub(
    text: String,
    buttonText: String,
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Здесь можно добавить Image(painterResource(R.drawable.no_network)...)
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = onButtonClick) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun PhotoGridShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_rect"
    )

    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        userScrollEnabled = false
    ) {
        items(6) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.7f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush)
            )
        }
    }
}

