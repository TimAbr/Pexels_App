package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import com.example.pexelsapp.domain.features.config.models.AppTheme
import androidx.compose.ui.res.stringResource
import com.example.pexelsapp.R


@Composable
fun ThemeSettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel
) {
    val currentTheme by viewModel.theme.collectAsState()

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
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ThemeCard(
                    theme = AppTheme.Light,
                    isSelected = currentTheme == AppTheme.Light,
                    icon = Icons.Default.BrightnessHigh,
                    label = stringResource(id = AppTheme.Light.toResourceId()),
                    onClick = { viewModel.onThemeSelected(AppTheme.Light) },
                    modifier = Modifier.weight(1f)
                )
                ThemeCard(
                    theme = AppTheme.Dark,
                    isSelected = currentTheme == AppTheme.Dark,
                    icon = Icons.Default.Brightness4,
                    label = stringResource(id = AppTheme.Dark.toResourceId()),
                    onClick = { viewModel.onThemeSelected(AppTheme.Dark) },
                    modifier = Modifier.weight(1f)
                )
                ThemeCard(
                    theme = AppTheme.System,
                    isSelected = currentTheme == AppTheme.System,
                    icon = Icons.Default.SettingsSuggest,
                    label = stringResource(id = AppTheme.System.toResourceId()),
                    onClick = { viewModel.onThemeSelected(AppTheme.System) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun ThemeCard(
    theme: AppTheme,
    isSelected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) 
                          else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    Column(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .run {
                if (isSelected) border(2.dp, borderColor, RoundedCornerShape(20.dp)) else this
            }
            .clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


