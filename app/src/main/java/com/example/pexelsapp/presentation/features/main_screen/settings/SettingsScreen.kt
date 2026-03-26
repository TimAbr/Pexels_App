package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import androidx.compose.ui.res.stringResource
import com.example.pexelsapp.R

fun AppLanguage.toResourceId(): Int = when (this) {
    AppLanguage.System -> R.string.lang_system
    AppLanguage.English -> R.string.lang_english
    AppLanguage.Russian -> R.string.lang_russian
}

fun AppTheme.toResourceId(): Int = when (this) {
    AppTheme.System -> R.string.theme_system
    AppTheme.Light -> R.string.theme_light
    AppTheme.Dark -> R.string.theme_dark
}

@Serializable
sealed class SettingsNav {
    @Serializable
    object Main : SettingsNav()
    @Serializable
    object Language : SettingsNav()
    @Serializable
    object Theme : SettingsNav()
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settingsNavController = rememberNavController()

    NavHost(
        navController = settingsNavController,
        startDestination = SettingsNav.Main
    ) {
        composable<SettingsNav.Main> {
            SettingsMainScreen(
                onLanguageClick = { settingsNavController.navigate(SettingsNav.Language) },
                onThemeClick = { settingsNavController.navigate(SettingsNav.Theme) },
                viewModel = viewModel
            )
        }
        composable<SettingsNav.Language> {
            LanguageSettingsScreen(
                onBack = { settingsNavController.popBackStack() },
                viewModel = viewModel
            )
        }
        composable<SettingsNav.Theme> {
            ThemeSettingsScreen(
                onBack = { settingsNavController.popBackStack() },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun SettingsMainScreen(
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    viewModel: SettingsViewModel
) {

    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        
        SettingsItem(
            title = stringResource(id = R.string.language), 
            subtitle = stringResource(id = language.toResourceId()),
            onClick = onLanguageClick
        )
        HorizontalDivider()
        SettingsItem(
            title = stringResource(id = R.string.theme), 
            subtitle = stringResource(id = theme.toResourceId()),
            onClick = onThemeClick
        )
    }
}



@Composable
fun SettingsSubScreen(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = title, style = MaterialTheme.typography.titleLarge)
        }
        content()
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp)
    ) {
        Text(text = title, style = MaterialTheme.typography.bodyLarge)
        if (subtitle != null) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


