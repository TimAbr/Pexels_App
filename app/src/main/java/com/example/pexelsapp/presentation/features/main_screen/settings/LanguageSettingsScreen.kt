package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.R
import com.example.pexelsapp.presentation.theme.PexelsAppTheme
import com.example.pexelsapp.utils.ThemedPreview

@Composable
fun LanguageSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val currentLanguage by viewModel.language.collectAsState()

    LanguageSettingsScreenContent(
        currentLanguage = currentLanguage,
        availableLanguages = viewModel.availableLanguages,
        onBack = onBack,
        onLanguageSelected = { viewModel.onLanguageSelected(it) }
    )
}

@Composable
private fun LanguageSettingsScreenContent(
    currentLanguage: AppLanguage,
    availableLanguages: List<AppLanguage>,
    onBack: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit
) {
    SettingsSubScreen(title = stringResource(id = R.string.language), onBack = onBack) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availableLanguages) { language ->
                LanguageItem(
                    language = language,
                    isSelected = currentLanguage == language,
                    onClick = { onLanguageSelected(language) }
                )
            }
        }
    }
}

@Composable
fun LanguageItem(
    language: AppLanguage,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = language.getDisplayName(),
                style = MaterialTheme.typography.titleMedium,
                color = contentColor
            )
            if (language != AppLanguage.System) {
                Text(
                    text = language.getNativeName(),
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor.copy(alpha = 0.6f)
                )
            }
        }
        
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

private fun AppLanguage.getNativeName(): String = when (this) {
    AppLanguage.English -> "English"
    AppLanguage.Russian -> "Русский"
    AppLanguage.System -> ""
}

@ThemedPreview
@Composable
private fun LanguageSettingsScreenPreview() {
    PexelsAppTheme {
        LanguageSettingsScreenContent(
            currentLanguage = AppLanguage.English,
            availableLanguages = listOf(AppLanguage.System, AppLanguage.English, AppLanguage.Russian),
            onBack = {},
            onLanguageSelected = {}
        )
    }
}
