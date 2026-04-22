package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.presentation.theme.PexelsAppTheme
import com.example.pexelsapp.utils.ThemedPreview

@Composable
fun ThemeSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val currentTheme by viewModel.theme.collectAsState()

    ThemeSettingsScreenContent(
        currentTheme = currentTheme,
        onBack = onBack,
        onThemeSelected = { viewModel.onThemeSelected(it) }
    )
}

@Composable
private fun ThemeSettingsScreenContent(
    currentTheme: AppTheme,
    onBack: () -> Unit,
    onThemeSelected: (AppTheme) -> Unit
) {
    SettingsSubScreen(title = stringResource(id = R.string.theme), onBack = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = stringResource(id = R.string.theme_choose),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ThemeCard(
                    isSelected = currentTheme == AppTheme.Light,
                    icon = Icons.Default.BrightnessHigh,
                    label = stringResource(id = AppTheme.Light.toResourceId()),
                    onClick = { onThemeSelected(AppTheme.Light) },
                    modifier = Modifier.weight(1f)
                )
                ThemeCard(
                    isSelected = currentTheme == AppTheme.Dark,
                    icon = Icons.Default.Brightness4,
                    label = stringResource(id = AppTheme.Dark.toResourceId()),
                    onClick = { onThemeSelected(AppTheme.Dark) },
                    modifier = Modifier.weight(1f)
                )
                ThemeCard(
                    isSelected = currentTheme == AppTheme.System,
                    icon = Icons.Default.SettingsSuggest,
                    label = stringResource(id = AppTheme.System.toResourceId()),
                    onClick = { onThemeSelected(AppTheme.System) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ThemeCard(
    isSelected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier
            .aspectRatio(0.9f)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@ThemedPreview
@Composable
private fun ThemeSettingsScreenPreview() {
    PexelsAppTheme {
        ThemeSettingsScreenContent(
            currentTheme = AppTheme.System,
            onBack = {},
            onThemeSelected = {}
        )
    }
}
