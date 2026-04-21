package org.example.project.domain.feature.auth.models

data class TokenPair(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
)
