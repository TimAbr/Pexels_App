package com.example.pexelsapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.config.usecases.GetAppLanguageUseCase
import com.example.pexelsapp.domain.features.config.usecases.GetAppThemeUseCase
import com.example.pexelsapp.domain.features.auth.usecases.ObserveAuthStateUseCase
import com.example.pexelsapp.domain.features.config.usecases.AreNotificationsEnabledUseCase
import com.example.pexelsapp.domain.features.config.usecases.HasAskedNotificationPermissionUseCase
import com.example.pexelsapp.domain.features.config.usecases.SetNotificationPermissionAskedUseCase
import com.example.pexelsapp.domain.features.config.usecases.SetNotificationsEnabledUseCase
import com.example.pexelsapp.presentation.features.notifications.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getAppThemeUseCase: GetAppThemeUseCase,
    private val getAppLanguageUseCase: GetAppLanguageUseCase,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    private val hasAskedNotificationPermissionUseCase: HasAskedNotificationPermissionUseCase,
    private val areNotificationsEnabledUseCase: AreNotificationsEnabledUseCase,
    private val setNotificationPermissionAskedUseCase: SetNotificationPermissionAskedUseCase,
    private val setNotificationsEnabledUseCase: SetNotificationsEnabledUseCase,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    val isAuthorized: StateFlow<Boolean> = observeAuthStateUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

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

    val hasAskedNotificationPermission: StateFlow<Boolean> = hasAskedNotificationPermissionUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    init {
        viewModelScope.launch {
            areNotificationsEnabledUseCase()
                .distinctUntilChanged()
                .collectLatest { enabled ->
                    if (enabled) {
                        notificationScheduler.scheduleDailyReminder()
                    } else {
                        notificationScheduler.cancelAllReminders()
                    }
                }
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        viewModelScope.launch {
            setNotificationPermissionAskedUseCase(true)
            setNotificationsEnabledUseCase(granted)
        }
    }
}
