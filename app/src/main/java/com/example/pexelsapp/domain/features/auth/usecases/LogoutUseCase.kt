package org.example.project.domain.feature.auth.usecases

import org.example.project.domain.feature.auth.repositories.AuthLogoutError
import org.example.project.domain.feature.auth.repositories.AuthRepository
import org.example.project.utils.models.Outcome

class LogoutUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Outcome<Unit, AuthLogoutError> =
        authRepository.logout()
}
