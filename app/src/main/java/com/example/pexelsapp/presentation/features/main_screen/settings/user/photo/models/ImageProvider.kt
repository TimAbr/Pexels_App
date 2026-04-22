package com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models

import com.example.pexelsapp.domain.features.user.models.Uri

interface ImageProvider {
    suspend fun getImage(): Uri?
}