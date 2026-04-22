package com.example.pexelsapp.domain.features.config.repositories

import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    fun getAppTheme(): Flow<AppTheme>
    suspend fun setAppTheme(theme: AppTheme)

    fun getAppLanguage(): Flow<AppLanguage>
    suspend fun setAppLanguage(language: AppLanguage)

    fun hasAskedNotificationPermission(): Flow<Boolean>
    suspend fun setNotificationPermissionAsked(asked: Boolean)

    fun areNotificationsEnabled(): Flow<Boolean>
    suspend fun setNotificationsEnabled(enabled: Boolean)
}
