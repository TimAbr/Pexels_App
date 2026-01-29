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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.domain.common.repositories.PhotosRepositoryError

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

@Composable
fun PhotoGrid(
    photos: List<Photo>,
    isPaginationLoading: Boolean,
    error: PhotosRepositoryError?,
    onPhotoClick: (Photo) -> Unit,
    onLoadMore: () -> Unit
) {
    val listState = rememberLazyStaggeredGridState()

    if (error == null) {
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastIndex ->
                    if (lastIndex != null && lastIndex >= photos.size - 5) {
                        onLoadMore()
                    }
                }
        }
    }

    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        state = listState,
        contentPadding = PaddingValues(24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalItemSpacing = 16.dp,
        modifier = Modifier.fillMaxSize()
    ) {
        items(photos) { photo ->
            PhotoCard(
                photo = photo,
                onClick = { onPhotoClick(photo) }
            )
        }

        if (isPaginationLoading) {
            item(span = StaggeredGridItemSpan.FullLine) {
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp).padding(16.dp)
                )
            }
        }

        if (error != null) {
            item(span = StaggeredGridItemSpan.FullLine) {
                RetrySection(onRetry = onLoadMore)
            }
        }
    }
}

@Composable
fun RetrySection(
    onRetry: ()->Unit
){
    HomeStub(
        text = stringResource(R.string.network_error),
        buttonText = stringResource(R.string.try_again),
        onButtonClick = onRetry
    )
}

@Composable
fun PhotoCard(photo: Photo, onClick: () -> Unit) {
    var isImageLoaded by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (isImageLoaded) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "photo_fade_in"
    )

    val aspectRatio = photo.width.toFloat() / photo.height.toFloat()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {

            if (!isImageLoaded) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_photo_placeholder),
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(0.33f),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AsyncImage(
                model = photo.source.original,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
                    .graphicsLayer(alpha = alpha),
                onSuccess = { isImageLoaded = true }
            )
        }
    }
}