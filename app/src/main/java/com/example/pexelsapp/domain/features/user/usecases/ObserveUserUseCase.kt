package com.example.pexelsapp.domain.features.user.usecases

import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.repositories.UserRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ObserveUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): StateFlow<User?> = userRepository.user
}
