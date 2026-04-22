package com.example.pexelsapp.presentation.features.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.presentation.features.auth.google.GoogleIdProvider
import com.example.pexelsapp.presentation.theme.PexelsAppTheme
import com.example.pexelsapp.utils.ThemedPreview
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    googleIdProvider: GoogleIdProvider,
    onNavigateToMain: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(state) {
        if (state is AuthScreenState.Authorized) {
            onNavigateToMain()
        }
    }

    AuthScreenContent(
        state = state,
        onLoginClick = {
            scope.launch {
                val googleIdOutcome = googleIdProvider.getId()
                when (googleIdOutcome) {
                    is Outcome.Success -> viewModel.loginWithGoogle(googleIdOutcome.value)
                    is Outcome.Error -> viewModel.onError(googleIdOutcome.type)
                }
            }
        },
        onClearError = viewModel::clearError,
    )
}

@Composable
private fun AuthScreenContent(
    state: AuthScreenState,
    onLoginClick: () -> Unit,
    onClearError: () -> Unit,
) {
    Scaffold { padding ->
        when (state) {
            is AuthScreenState.Loading -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = true,
                    onLoginClick = {},
                )
            }
            is AuthScreenState.LogIn -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = false,
                    onLoginClick = onLoginClick,
                )
            }
            is AuthScreenState.Error -> {
                AuthContent(
                    modifier = Modifier.padding(padding),
                    isLoading = false,
                    onLoginClick = onLoginClick,
                )
                AuthErrorDialog(
                    error = state.error,
                    onDismiss = onClearError,
                )
            }
            is AuthScreenState.Authorized -> {
            }
        }
    }
}

@Composable
private fun AuthContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    onLoginClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = stringResource(R.string.auth_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 16.dp),
        )

        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(R.drawable.google_logo),
                contentDescription = "Google Logo",
                modifier = Modifier.size(140.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.outlineVariant),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                )
            } else {
                Text(
                    text = stringResource(R.string.auth_google_button),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(bounded = true),
                        ) {
                            onLoginClick()
                        }
                        .padding(8.dp),
                )
            }
        }
    }
}

@Composable
private fun AuthErrorDialog(
    error: AuthLoginError,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.auth_error),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = mapError(error),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
            )
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
            ) {
                Text(
                    text = stringResource(R.string.common_ok),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
    )
}

@Composable
fun mapError(error: AuthLoginError): String {
    val resId = when (error) {
        is AuthLoginError.Common.NetworkError -> R.string.error_network
        is AuthLoginError.Common.ServerError -> R.string.error_server
        is AuthLoginError.Common.Canceled -> R.string.error_canceled
        is AuthLoginError.Common.Unknown -> R.string.error_unknown
        is AuthLoginError.GoogleAuthError.NoCredentials -> R.string.error_no_credentials
        is AuthLoginError.GoogleAuthError.InvalidToken -> R.string.error_unknown
        else -> R.string.error_unknown
    }
    return stringResource(resId)
}

@ThemedPreview
@Composable
private fun AuthScreenLogInPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    PexelsAppTheme {
        AuthScreenContent(
            state = AuthScreenState.LogIn,
            onLoginClick = {},
            onClearError = {},
        )
    }
}

@ThemedPreview
@Composable
private fun AuthScreenLoadingPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    PexelsAppTheme {
        AuthScreenContent(
            state = AuthScreenState.Loading,
            onLoginClick = {},
            onClearError = {},
        )
    }
}

@ThemedPreview
@Composable
private fun AuthScreenErrorPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    PexelsAppTheme {
        AuthScreenContent(
            state = AuthScreenState.Error(AuthLoginError.Common.NetworkError),
            onLoginClick = {},
            onClearError = {},
        )
    }
}
