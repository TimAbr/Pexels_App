package com.example.pexelsapp.domain.features.config.models

sealed class AppLanguage(val code: String) {
    object System : AppLanguage("system")
    object English : AppLanguage("en")
    object Russian : AppLanguage("ru")
}
