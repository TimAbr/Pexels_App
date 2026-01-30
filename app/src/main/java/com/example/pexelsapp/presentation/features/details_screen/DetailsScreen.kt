package com.example.pexelsapp.presentation.features.details_screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
            .padding(horizontal = 24.dp)
    ) {

        DetailsScreenHeader(onBack = onBack, uiState = uiState)

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
fun DetailsScreenHeader(
    onBack: () -> Unit,
    uiState: DetailsUiState
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            contentAlignment = Alignment.Center
        ) {
            FilledIconButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart),
                shape = MaterialTheme.shapes.medium,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            val authorName = (uiState as? DetailsUiState.Content)?.photo?.photographer?.name ?: ""
            Text(
                text = authorName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        if (uiState is DetailsUiState.Loading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        } else {
            Spacer(modifier = Modifier.height(4.dp))
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
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.Center

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

            DownloadButton(
                onDownloadClick = onDownloadClick
            )

            BookmarkButton(
                onBookmarkClick = onBookmarkClick,
                isBookmarked = isBookmarked
            )
        }
    }
}


@Composable
fun DownloadButton(
    onDownloadClick: () -> Unit
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
            text = stringResource(R.string.download_button_text),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            ),
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}


@Composable
fun BookmarkButton(
    onBookmarkClick: () -> Unit,
    isBookmarked: Boolean
){
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