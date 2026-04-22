package com.example.pexelsapp.presentation.features.auth.mappers

import android.content.Context
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AuthErrorMapper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun map(error: AuthLoginError): String {
        val resId = when (error) {
            is AuthLoginError.Common.NetworkError -> R.string.error_network
            is AuthLoginError.Common.ServerError -> R.string.error_server
            is AuthLoginError.Common.Canceled -> R.string.error_canceled
            is AuthLoginError.Common.Unknown -> R.string.error_unknown
            is AuthLoginError.GoogleAuthError.NoCredentials -> R.string.error_no_credentials
            is AuthLoginError.GoogleAuthError.InvalidToken -> R.string.error_unknown // or specific if available
            else -> R.string.error_unknown
        }
        return context.getString(resId)
    }
}
