package com.example.pexelsapp.presentation.features.auth.mappers

import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.NoCredentialException
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

fun Exception.toAuthLoginError(): AuthLoginError {
    return when (this) {
        is GetCredentialCancellationException -> AuthLoginError.Common.Canceled
        is NoCredentialException -> AuthLoginError.GoogleAuthError.NoCredentials
        is GetCredentialInterruptedException -> AuthLoginError.Common.Canceled
        is GetCredentialProviderConfigurationException -> AuthLoginError.Common.ServerError
        is GoogleIdTokenParsingException -> AuthLoginError.GoogleAuthError.InvalidToken
        is GetCredentialException -> AuthLoginError.Common.NetworkError
        else -> AuthLoginError.Common.Unknown
    }
}
