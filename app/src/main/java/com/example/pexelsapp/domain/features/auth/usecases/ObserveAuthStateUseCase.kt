package org.example.project.domain.feature.auth.usecases

import kotlinx.coroutines.flow.StateFlow
import org.example.project.domain.feature.auth.repositories.AuthRepository

class ObserveAuthStateUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): StateFlow<Boolean> =
        authRepository.isAuthorized
}
