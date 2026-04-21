package com.example.pexelsapp.domain.features.auth.usecases

import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository
import javax.inject.Inject

class GetAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Boolean =
        authRepository.isAuthorized.value
}
