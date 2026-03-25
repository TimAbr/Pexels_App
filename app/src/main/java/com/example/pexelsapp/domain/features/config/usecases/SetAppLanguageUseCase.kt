package com.example.pexelsapp.domain.features.config.usecases

import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.repositories.AppConfigRepository
import javax.inject.Inject

class SetAppLanguageUseCase @Inject constructor(
    private val repository: AppConfigRepository
) {
    suspend operator fun invoke(language: AppLanguage) {
        repository.setAppLanguage(language)
    }
}
