package com.example.pexelsapp.domain.features.config.repositories

import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    fun getAppTheme(): Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)

    fun getAppLanguage(): Flow<AppLanguage>
    suspend fun setAppLanguage(language: AppLanguage)
}
