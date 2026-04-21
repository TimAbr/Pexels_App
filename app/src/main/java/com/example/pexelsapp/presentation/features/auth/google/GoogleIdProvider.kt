package com.example.pexelsapp.presentation.features.auth.google

import com.example.pexelsapp.domain.features.auth.models.GoogleIdToken
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.utils.models.Outcome

interface GoogleIdProvider {
    suspend fun getId(): Outcome<GoogleIdToken, AuthLoginError>
}
