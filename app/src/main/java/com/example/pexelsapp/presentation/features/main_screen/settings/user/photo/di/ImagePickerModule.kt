package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.di

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.CameraImageProvider
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.GalleryImageProvider
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.ImageProvider
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.ImageProviderType

@Module
@InstallIn(ActivityRetainedComponent::class)
object ImagePickerModule {

    @Provides
    @IntoMap
    @ImageProviderKey(ImageProviderType.GALLERY)
    fun provideGalleryProvider(): ImageProvider {
        return GalleryImageProvider()
    }

    @Provides
    @IntoMap
    @ImageProviderKey(ImageProviderType.CAMERA)
    fun provideCameraProvider(): ImageProvider {
        return CameraImageProvider()
    }
}