package com.example.pexelsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.config.usecases.GetAppLanguageUseCase
import com.example.pexelsapp.domain.features.config.usecases.GetAppThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getAppThemeUseCase: GetAppThemeUseCase,
    getAppLanguageUseCase: GetAppLanguageUseCase
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
}
