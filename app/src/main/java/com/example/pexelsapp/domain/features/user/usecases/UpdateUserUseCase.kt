package com.example.pexelsapp.domain.features.user.usecases

import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.repositories.UserError
import com.example.pexelsapp.domain.features.user.repositories.UserRepository
import com.example.pexelsapp.utils.models.Outcome
import javax.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(user: User): Outcome<Unit, UserError.Update> =
        userRepository.updateProfile(user)
}
