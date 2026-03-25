package com.example.pexelsapp.data.datasources.config

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ConfigSource @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        const val APP_THEME_KEY = "app_theme"
        const val APP_LANGUAGE_KEY = "app_language"

        val APP_THEME = stringPreferencesKey(APP_THEME_KEY)
        val APP_LANGUAGE = stringPreferencesKey(APP_LANGUAGE_KEY)
    }

    fun getAppTheme(): Flow<AppTheme> = dataStore.data.map { preferences ->
        when (preferences[APP_THEME]) {
            "Light" -> AppTheme.Light
            "Dark" -> AppTheme.Dark
            else -> AppTheme.System
        }
    }

    suspend fun setAppTheme(theme: AppTheme) {
        val themeName = when (theme) {
            AppTheme.Light -> "Light"
            AppTheme.Dark -> "Dark"
            AppTheme.System -> "System"
        }
        dataStore.edit { it[APP_THEME] = themeName }
    }

    fun getAppLanguage(): Flow<AppLanguage> = dataStore.data.map { preferences ->
        when (preferences[APP_LANGUAGE]) {
            "ru" -> AppLanguage.Russian
            "en" -> AppLanguage.English
            else -> AppLanguage.System
        }
    }

    suspend fun setAppLanguage(language: AppLanguage) {
        dataStore.edit { it[APP_LANGUAGE] = language.code }
    }
}

