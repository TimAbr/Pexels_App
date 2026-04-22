package com.example.pexelsapp.presentation.features.main_screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.example.pexelsapp.R
import com.example.pexelsapp.domain.features.config.models.AppLanguage
import com.example.pexelsapp.domain.features.config.models.AppTheme
import com.example.pexelsapp.domain.features.user.models.User
import com.example.pexelsapp.presentation.theme.PexelsAppTheme
import com.example.pexelsapp.utils.ThemedPreview
import kotlinx.serialization.Serializable

@Serializable
sealed class SettingsNav {
    @Serializable
    object Main : SettingsNav()
    @Serializable
    object Language : SettingsNav()
    @Serializable
    object Theme : SettingsNav()
    @Serializable
    object Account : SettingsNav()
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
                onAccountClick = { settingsNavController.navigate(SettingsNav.Account) },
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
        composable<SettingsNav.Account> {
            AccountSettingsScreen(
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
    onAccountClick: () -> Unit,
    viewModel: SettingsViewModel
) {
    val theme by viewModel.theme.collectAsState()
    val language by viewModel.language.collectAsState()
    val user by viewModel.user.collectAsState()

    SettingsMainScreenContent(
        theme = theme,
        language = language,
        user = user,
        onLanguageClick = onLanguageClick,
        onThemeClick = onThemeClick,
        onAccountClick = onAccountClick
    )
}

@Composable
private fun SettingsMainScreenContent(
    theme: AppTheme,
    language: AppLanguage,
    user: User?,
    onLanguageClick: () -> Unit,
    onThemeClick: () -> Unit,
    onAccountClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        ProfileHeader(user = user, onClick = onAccountClick)
        
        Spacer(modifier = Modifier.height(32.dp))

        SettingsItem(
            title = stringResource(id = R.string.language),
            subtitle = language.getDisplayName(),
            icon = Icons.Default.Language,
            onClick = onLanguageClick
        )
        HorizontalDivider(modifier = Modifier.padding(start = 48.dp), thickness = 0.5.dp)
        
        SettingsItem(
            title = stringResource(id = R.string.theme),
            subtitle = stringResource(id = theme.toResourceId()),
            icon = Icons.Default.Palette,
            onClick = onThemeClick
        )
    }
}

@Composable
fun ProfileHeader(
    user: User?,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!user?.photoUrl.isNullOrEmpty()) {
            AsyncImage(
                model = user?.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.outline
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user?.name ?: stringResource(R.string.profile_guest),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = user?.email ?: stringResource(R.string.profile_sign_in_hint),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
        Text(
            text = label, 
            style = MaterialTheme.typography.labelMedium, 
            color = MaterialTheme.colorScheme.primary
        )
        Text(text = value, style = MaterialTheme.typography.bodyLarge)
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
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
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

fun AppTheme.toResourceId(): Int = when (this) {
    AppTheme.System -> R.string.theme_system
    AppTheme.Light -> R.string.theme_light
    AppTheme.Dark -> R.string.theme_dark
}

@Composable
fun AppLanguage.getDisplayName(): String = when (this) {
    AppLanguage.System -> stringResource(R.string.lang_system)
    AppLanguage.English -> "English"
    AppLanguage.Russian -> "Русский"
}

@ThemedPreview
@Composable
private fun SettingsMainScreenPreview() {
    PexelsAppTheme {
        SettingsMainScreenContent(
            theme = AppTheme.System,
            language = AppLanguage.English,
            user = User("1", "test@test.com", "John Doe", ""),
            onLanguageClick = {},
            onThemeClick = {},
            onAccountClick = {}
        )
    }
}

@ThemedPreview
@Composable
private fun SettingsMainScreenGuestPreview() {
    PexelsAppTheme {
        SettingsMainScreenContent(
            theme = AppTheme.System,
            language = AppLanguage.Russian,
            user = null,
            onLanguageClick = {},
            onThemeClick = {},
            onAccountClick = {}
        )
    }
}
