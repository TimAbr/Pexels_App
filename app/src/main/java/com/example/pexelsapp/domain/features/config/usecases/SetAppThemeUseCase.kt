package com.example.pexelsapp.domain.features.config.usecases

import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.config.repositories.AppConfigRepository
import javax.inject.Inject

class SetAppThemeUseCase @Inject constructor(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(theme: AppTheme) {
        repository.setAppTheme(theme)
    }
}
