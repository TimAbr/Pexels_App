package com.example.pexelsapp.domain.features.auth.models

data class TokenPair(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken,
)
