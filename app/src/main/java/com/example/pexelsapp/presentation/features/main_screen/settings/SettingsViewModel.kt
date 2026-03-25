package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.config.usecases.GetAppLanguageUseCase
import com.example.pexelsapp.domain.features.config.usecases.GetAppThemeUseCase
import com.example.pexelsapp.domain.features.config.usecases.SetAppLanguageUseCase
import com.example.pexelsapp.domain.features.config.usecases.SetAppThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getAppThemeUseCase: GetAppThemeUseCase,
    getAppLanguageUseCase: GetAppLanguageUseCase,
    private val setAppThemeUseCase: SetAppThemeUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase
) : ViewModel() {

    val theme: StateFlow<AppTheme> = getAppThemeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppTheme.System
        )

    val language: StateFlow<AppLanguage> = getAppLanguageUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AppLanguage.System
        )

    val availableThemes = listOf(AppTheme.System, AppTheme.Light, AppTheme.Dark)
    val availableLanguages = listOf(AppLanguage.System, AppLanguage.English, AppLanguage.Russian)

    fun onThemeSelected(theme: AppTheme) {
        viewModelScope.launch {
            setAppThemeUseCase(theme)
        }
    }

    fun onLanguageSelected(language: AppLanguage) {
        viewModelScope.launch {
            setAppLanguageUseCase(language)
        }
    }
}
