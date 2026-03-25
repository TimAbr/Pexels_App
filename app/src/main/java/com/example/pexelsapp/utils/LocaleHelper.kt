package com.example.pexelsapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import java.util.Locale

object LocaleHelper {
    fun updateBaseContextLocale(context: Context, language: AppLanguage): Context {
        val locale = when (language) {
            AppLanguage.English -> Locale("en")
            AppLanguage.Russian -> Locale("ru")
            AppLanguage.System -> Locale.getDefault()
        }
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val localizedBase = context.createConfigurationContext(configuration)

        // Return a wrapper that connects back to the original context (the Activity)
        // so Hilt can still find the Activity through the context wrapper chain.
        return object : ContextWrapper(context) {
            override fun getResources(): Resources = localizedBase.resources
            override fun getAssets(): AssetManager = localizedBase.assets
        }
    }
}
