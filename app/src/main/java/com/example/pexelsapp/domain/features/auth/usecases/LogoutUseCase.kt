package com.example.pexelsapp.domain.features.auth.usecases

import com.example.pexelsapp.utils.models.Outcome
import com.example.pexelsapp.domain.features.auth.repositories.AuthLogoutError
import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Outcome<Unit, AuthLogoutError> =
        authRepository.logout()
}
