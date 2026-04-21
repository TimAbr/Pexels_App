package com.example.pexelsapp.domain.features.auth.usecases

import com.example.pexelsapp.domain.features.auth.repositories.AuthLogoutError
import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository
import com.example.pexelsapp.utils.models.Outcome
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Outcome<Unit, AuthLogoutError> =
        authRepository.logout()
}
