package com.example.pexelsapp.presentation.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pexelsapp.presentation.features.auth.AuthScreen
import com.example.pexelsapp.presentation.features.auth.AuthViewModel
import com.example.pexelsapp.presentation.features.auth.google.GoogleIdProvider
import com.example.pexelsapp.presentation.features.details_screen.DetailsScreen
import com.example.pexelsapp.presentation.features.main_screen.MainContainer
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import kotlinx.serialization.Serializable

@EntryPoint
@InstallIn(ActivityComponent::class)
interface AuthEntryPoint {
    fun googleIdProvider(): GoogleIdProvider
}

@Serializable
sealed class RootGraph {
    @Serializable
    object Auth : RootGraph()
    @Serializable
    object Main : RootGraph()
    @Serializable
    data class Details(val photoId: Long) : RootGraph()
}

@Serializable
sealed class MainNav {
    @Serializable
    object Home : MainNav()
    @Serializable
    object Bookmarks : MainNav()
    @Serializable
    object Settings : MainNav()
}

@Composable
fun RootNavigation(
    isAuthorized: Boolean
) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = if (isAuthorized) RootGraph.Main else RootGraph.Auth
    ) {
        composable<RootGraph.Auth> {
            val viewModel: AuthViewModel = hiltViewModel()
            val context = LocalContext.current
            val googleIdProvider = remember(context) {
                EntryPointAccessors.fromActivity(
                    context as Activity,
                    AuthEntryPoint::class.java
                ).googleIdProvider()
            }
            
            AuthScreen(
                viewModel = viewModel,
                googleIdProvider = googleIdProvider,
                onNavigateToMain = {
                    rootNavController.navigate(RootGraph.Main) {
                        popUpTo(RootGraph.Auth) { inclusive = true }
                    }
                }
            )
        }

        composable<RootGraph.Main> {
            MainContainer(rootNavController)
        }

        composable<RootGraph.Details> { backStackEntry ->
            val details: RootGraph.Details = backStackEntry.toRoute()
            DetailsScreen(photoId = details.photoId, onBack = {
                rootNavController.popBackStack()
            })
        }
    }
}