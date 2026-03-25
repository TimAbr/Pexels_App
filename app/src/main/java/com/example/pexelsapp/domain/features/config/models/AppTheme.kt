package com.example.pexelsapp.domain.features.config.models

sealed class AppTheme {
    object System : AppTheme()
    object Light : AppTheme()
    object Dark : AppTheme()
}
