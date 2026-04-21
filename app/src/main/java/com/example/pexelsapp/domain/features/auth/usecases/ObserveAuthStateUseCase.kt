package com.example.pexelsapp.domain.features.auth.usecases

import kotlinx.coroutines.flow.StateFlow
import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository
import javax.inject.Inject

class ObserveAuthStateUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): StateFlow<Boolean> =
        authRepository.isAuthorized
}
