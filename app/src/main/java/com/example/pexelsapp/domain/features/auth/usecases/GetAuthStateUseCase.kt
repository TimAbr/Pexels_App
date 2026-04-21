package com.example.pexelsapp.domain.features.auth.usecases

import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository

class GetAuthStateUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Boolean =
        authRepository.isAuthorized.value
}
