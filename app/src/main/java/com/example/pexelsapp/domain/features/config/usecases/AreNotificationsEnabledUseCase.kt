package com.example.pexelsapp.domain.features.config.usecases

import com.example.pexelsapp.domain.features.config.repositories.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AreNotificationsEnabledUseCase @Inject constructor(
    private val repository: AppConfigRepository
) {
    operator fun invoke(): Flow<Boolean> = repository.areNotificationsEnabled()
}
