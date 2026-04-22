package com.example.pexelsapp

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.pexelsapp.presentation.features.auth.google.GoogleIdProvider
import com.example.pexelsapp.presentation.features.auth.google.LocalGoogleIdProvider
import com.example.pexelsapp.presentation.features.main_screen.settings.user.photo.models.ImagePickerManager
import com.example.pexelsapp.presentation.navigation.RootNavigation
import com.example.pexelsapp.presentation.theme.PexelsAppTheme
import com.example.pexelsapp.utils.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @Inject
    lateinit var googleIdProvider: GoogleIdProvider

    @Inject
    lateinit var imagePickerManager: ImagePickerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        imagePickerManager.bind(this)

        installSplashScreen()

        enableEdgeToEdge()
        setContent {
            val appTheme by viewModel.theme.collectAsState()
            val appLanguage by viewModel.language.collectAsState()

            val context = LocalContext.current
            val localizedContext = LocaleHelper.updateBaseContextLocale(context, appLanguage)

            val hasAskedPermission by viewModel.hasAskedNotificationPermission.collectAsState()

            if (!hasAskedPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { granted ->
                    viewModel.onNotificationPermissionResult(granted)
                }
                LaunchedEffect(Unit) {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            CompositionLocalProvider(
                LocalContext provides localizedContext,
                LocalConfiguration provides localizedContext.resources.configuration,
                LocalGoogleIdProvider provides googleIdProvider,
            ) {

                PexelsAppTheme(appTheme = appTheme) {
                    val isAuthorized by viewModel.isAuthorized.collectAsState()
                    RootNavigation(isAuthorized = isAuthorized)
                }
            }
        }
    }
}
