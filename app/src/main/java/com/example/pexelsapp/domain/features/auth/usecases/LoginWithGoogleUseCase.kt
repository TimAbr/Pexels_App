package com.example.pexelsapp.domain.features.auth.usecases

import com.example.pexelsapp.domain.features.auth.models.AuthMethod
import com.example.pexelsapp.domain.features.auth.models.GoogleIdToken
import com.example.pexelsapp.domain.features.auth.repositories.AuthLoginError
import com.example.pexelsapp.domain.features.auth.repositories.AuthRepository
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.domain.features.user.repositories.UserRepository
import com.example.pexelsapp.utils.models.Outcome
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(idToken: GoogleIdToken): Outcome<Unit, AuthLoginError.GoogleAuthError> {
        val loginResult = authRepository.login(AuthMethod.Google(idToken))
        
        if (loginResult is Outcome.Success) {
            syncUserProfile()
        }
        
        return loginResult
    }

    private suspend fun syncUserProfile() {
        val user = userRepository.user.first { it != null } ?: return

        val existingUserOutcome = userRepository.getUserById(user.id)
        
        if (existingUserOutcome is Outcome.Error && existingUserOutcome.type is UserError.Get.NotFound) {
            userRepository.updateProfile(user)
        }
    }
}
