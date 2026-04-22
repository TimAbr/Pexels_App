package com.example.pexelsapp.utils

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import androidx.core.os.ConfigurationCompat
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import java.util.Locale

object LocaleHelper {
    fun getLocale(language: AppLanguage): Locale {
        return when (language) {
            AppLanguage.English -> Locale.forLanguageTag("en")
            AppLanguage.Russian -> Locale.forLanguageTag("ru")
            AppLanguage.System -> getSystemLocale()
        }
    }

    fun getSystemLocale(): Locale {
        return ConfigurationCompat.getLocales(Resources.getSystem().configuration)[0]
            ?: Locale.getDefault()
    }

    fun getCurrentLocale(configuration: Configuration): Locale {
        return ConfigurationCompat.getLocales(configuration)[0] ?: Locale.getDefault()
    }

    fun updateBaseContextLocale(context: Context, language: AppLanguage): Context {
        val locale = getLocale(language)
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)

        val localizedBase = context.createConfigurationContext(configuration)

        return object : ContextWrapper(context) {
            override fun getResources(): Resources = localizedBase.resources
            override fun getAssets(): AssetManager = localizedBase.assets
        }
    }
}
