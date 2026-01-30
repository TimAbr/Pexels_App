package com.example.pexelsapp.presentation.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.pexelsapp.presentation.features.details_screen.DetailsScreen
import com.example.pexelsapp.presentation.features.main_screen.MainContainer
import kotlinx.serialization.Serializable

@Serializable
sealed class RootGraph {
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
}

@Composable
fun RootNavigation() {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        startDestination = RootGraph.Main
    ) {
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