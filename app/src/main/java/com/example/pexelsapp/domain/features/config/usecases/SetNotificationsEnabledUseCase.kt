package com.example.pexelsapp.domain.features.config.usecases

import com.example.pexelsapp.domain.features.config.repositories.AppConfigRepository
import javax.inject.Inject

class SetNotificationsEnabledUseCase @Inject constructor(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(enabled: Boolean) = repository.setNotificationsEnabled(enabled)
}
