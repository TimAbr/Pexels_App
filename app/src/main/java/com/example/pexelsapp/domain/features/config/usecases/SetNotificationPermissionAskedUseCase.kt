package com.example.pexelsapp.domain.features.config.usecases

import com.example.pexelsapp.domain.features.config.repositories.AppConfigRepository
import javax.inject.Inject

class SetNotificationPermissionAskedUseCase @Inject constructor(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(asked: Boolean) = repository.setNotificationPermissionAsked(asked)
}
