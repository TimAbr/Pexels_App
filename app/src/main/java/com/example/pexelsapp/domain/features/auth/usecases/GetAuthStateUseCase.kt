package org.example.project.domain.feature.auth.usecases

import org.example.project.domain.feature.auth.repositories.AuthRepository

class GetAuthStateUseCase(
    private val authRepository: AuthRepository,
) {
    operator fun invoke(): Boolean =
        authRepository.isAuthorized.value
}
