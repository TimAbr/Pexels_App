package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.config.usecases.GetAppLanguageUseCase
import com.example.pexelsapp.domain.features.config.usecases.GetAppThemeUseCase
import com.example.pexelsapp.domain.features.config.usecases.SetAppLanguageUseCase
import com.example.pexelsapp.domain.features.config.usecases.SetAppThemeUseCase
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.domain.features.user.usecases.ObserveUserUseCase
import com.example.pexelsapp.domain.features.user.usecases.UpdateUserUseCase
import com.example.pexelsapp.domain.features.user.usecases.UpdateUserPhotoUseCase
import com.example.pexelsapp.domain.features.auth.usecases.LogoutUseCase
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.ImagePickerManager
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.ImageProviderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getAppThemeUseCase: GetAppThemeUseCase,
    getAppLanguageUseCase: GetAppLanguageUseCase,
    private val setAppThemeUseCase: SetAppThemeUseCase,
    private val setAppLanguageUseCase: SetAppLanguageUseCase,
    observeUserUseCase: ObserveUserUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val updateUserPhotoUseCase: UpdateUserPhotoUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val imagePickerManager: ImagePickerManager,
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

    val user: StateFlow<User?> = observeUserUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
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

    fun updateName(newName: String) {
        val currentUser = user.value ?: return
        viewModelScope.launch {
            updateUserUseCase(currentUser.copy(name = newName))
        }
    }

    fun pickImageFromGallery() {
        viewModelScope.launch {
            val uri = imagePickerManager.getImage(ImageProviderType.GALLERY)
            uri?.let { updateUserPhotoUseCase(it) }
        }
    }

    fun takePhotoFromCamera() {
        viewModelScope.launch {
            val uri = imagePickerManager.getImage(ImageProviderType.CAMERA)
            uri?.let { updateUserPhotoUseCase(it) }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }
}
