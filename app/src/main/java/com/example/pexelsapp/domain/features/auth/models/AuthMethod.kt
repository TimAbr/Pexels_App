package com.example.pexelsapp.domain.features.auth.models

import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError

sealed interface AuthMethod<out E : AuthLoginError> {
    data class Google(val idToken: GoogleIdToken) : AuthMethod<AuthLoginError.GoogleAuthError>
}
