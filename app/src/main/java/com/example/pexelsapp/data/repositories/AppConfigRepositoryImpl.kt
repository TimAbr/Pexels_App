package com.example.pexelsapp.data.repositories

import com.example.pexelsapp.data.datasources.config.ConfigSource
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.config.repositories.AppConfigRepository
import dagger.hilt.components.SingletonComponent
import it.czerwinski.android.hilt.annotations.BoundTo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@BoundTo(supertype = AppConfigRepository::class, component = SingletonComponent::class)
class AppConfigRepositoryImpl @Inject constructor(
    private val configSource: ConfigSource
) : AppConfigRepository {

    override fun getAppTheme(): Flow<AppTheme> = configSource.getAppTheme()

    override suspend fun setAppTheme(theme: AppTheme) {
        configSource.setAppTheme(theme)
    }

    override fun getAppLanguage(): Flow<AppLanguage> = configSource.getAppLanguage()

    override suspend fun setAppLanguage(language: AppLanguage) {
        configSource.setAppLanguage(language)
    }
}

