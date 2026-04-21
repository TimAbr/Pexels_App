package org.example.project.domain.feature.auth.usecases

import org.example.project.domain.feature.auth.models.AuthMethod
import org.example.project.domain.feature.auth.repositories.AuthLoginError
import org.example.project.domain.feature.auth.repositories.AuthRepository
import org.example.project.utils.models.Outcome

class LoginWithGoogleUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Outcome<Unit, AuthLoginError> =
        authRepository.login(AuthMethod.Google)
}
