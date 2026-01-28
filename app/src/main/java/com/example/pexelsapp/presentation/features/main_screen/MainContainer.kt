package com.example.pexelsapp.presentation.features.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pexelsapp.presentation.navigation.MainNav
import com.example.pexelsapp.presentation.navigation.RootGraph

@Composable
fun MainContainer(rootNavController: NavController) {
    val internalNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppBottomNavigation(internalNavController)
        }
    ) { padding ->
        NavHost(
            navController = internalNavController,
            startDestination = MainNav.Home,
            modifier = Modifier.padding(padding)
        ) {
            composable<MainNav.Home> {
                HomeScreen(
                    onPhotoClick = { photo ->
                        rootNavController.navigate(RootGraph.Details(photo))
                    }
                )
            }
            composable<MainNav.Bookmarks> {
                BookmarksScreen(
                    onPhotoClick = { id ->
                        rootNavController.navigate(RootGraph.Details(id))
                    }
                )
            }
        }
    }
}

@Composable
fun AppBottomNavigation(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Bookmarks
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination
                ?.hierarchy
                ?.any { it.hasRoute(item.route::class) }
                ?: (item.route is MainNav.Home)

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (isSelected) {
                            Box(
                                Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(2.dp))
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                        Icon(item.icon, contentDescription = null)
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}

sealed class NavigationItem(val route: Any, val icon: ImageVector) {
    object Home : NavigationItem(MainNav.Home, Icons.Default.Home)
    object Bookmarks : NavigationItem(MainNav.Bookmarks, Icons.Default.FavoriteBorder)
}
