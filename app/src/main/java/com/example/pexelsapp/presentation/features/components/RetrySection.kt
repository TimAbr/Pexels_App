package com.example.pexelsapp.presentation.features.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.pexelsapp.R

@Composable
fun RetrySection(
    onRetry: () -> Unit
) {
    ScreenStub(
        text = stringResource(R.string.network_error),
        buttonText = stringResource(R.string.try_again),
        onButtonClick = onRetry
    )
}
