package com.example.pexelsapp.presentation.features.details_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.common.models.Photo
import com.example.pexelsapp.presentation.features.components.ScreenStub
import com.example.pexelsapp.presentation.features.components.getBestUrlForHeight
import kotlin.compareTo
import kotlin.times

@Composable
fun DetailsScreen(
    photoId: Long,
    onBack: () -> Unit,
    viewModel: DetailsScreenViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            val authorName = (uiState as? DetailsUiState.Content)?.photo?.photographer?.name ?: ""
            Text(
                text = authorName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        if (uiState is DetailsUiState.Loading) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (val state = uiState) {
                is DetailsUiState.Content -> {
                    PhotoContent(
                        photo = state.photo,
                        isBookmarked = state.isBookmarked,
                        onBookmarkClick = viewModel::onBookmarkClick,
                        onDownloadClick = viewModel::onDownloadClick
                    )
                }
                is DetailsUiState.NotFound -> {
                    ScreenStub(
                        text = "Image not found",
                        buttonText = "Explore",
                        onButtonClick = onBack
                    )
                }
                is DetailsUiState.Error -> {
                    ScreenStub(
                        text = state.message,
                        onButtonClick = viewModel::tryAgain,
                        buttonText = "Try Again"
                    )
                }
                else -> Unit
            }
        }
    }
}

@Composable
fun PhotoContent(
    photo: Photo,
    isBookmarked: Boolean,
    onBookmarkClick: () -> Unit,
    onDownloadClick: () -> Unit
) {


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 24.dp)
    ) {
        ZoomablePhotoCard(photo = photo, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(48.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onDownloadClick() },
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)

                ) {
                    Icon(
                        painter = painterResource(R.drawable.round_file_download_24),
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "Download",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

            }

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable { onBookmarkClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(
                        id = if (isBookmarked) R.drawable.bookmark_button_active
                        else R.drawable.bookmark_button_inactive
                    ),
                    contentDescription = null,
                    tint = if (isBookmarked) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ZoomablePhotoCard(
    photo: Photo,
    modifier: Modifier
){
    var scale by remember { mutableFloatStateOf(1f) }
    val animatedScale by animateFloatAsState(
        targetValue = scale,
        label = "zoom_reset_anim"
    )
    var isImageLoaded by remember { mutableStateOf(false) }

    val aspectRatio = photo.width.toFloat() / photo.height.toFloat()

    Card(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(MaterialTheme.shapes.large)
    ){
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
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photo.source.original)
                    .crossfade(600)
                    .build(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer(
                        scaleX = animatedScale,
                        scaleY = animatedScale
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 4f)
                        }
                    }
                    .clickable(
                        interactionSource = null,
                        indication = null,
                        enabled = scale > 1f
                    ) { scale = 1f },
                contentScale = ContentScale.Fit
            )
        }
    }

}