package com.example.pexelsapp.domain.features.auth.usecases

import com.example.pexelsapp.utils.models.Outcome
import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository

class LoginWithGoogleUseCase(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(): Outcome<Unit, AuthLoginError.GoogleAuthError> =
        authRepository.login(AuthMethod.Google)
}
