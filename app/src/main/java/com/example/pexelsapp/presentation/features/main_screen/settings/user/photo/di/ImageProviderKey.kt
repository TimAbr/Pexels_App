package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.di// presentation/di/ImageProviderKey.kt

import dagger.MapKey
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.ImageProviderType

@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class ImageProviderKey(val value: ImageProviderType)