package com.example.pexelsapp.presentation.features.main_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pexelsapp.R
import com.example.pexelsapp.presentation.features.main_screen.bookmarks.BookmarksScreen
import com.example.pexelsapp.presentation.features.main_screen.home.HomeScreen
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
                        rootNavController.navigate(RootGraph.Details(photo.id))
                    }
                )
            }
            composable<MainNav.Bookmarks> {
                BookmarksScreen(
                    onPhotoClick = { photo ->
                        rootNavController.navigate(RootGraph.Details(photo.id))
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
        tonalElevation = 0.dp,
        modifier = Modifier.height(70.dp)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { item ->
            val isSelected = currentDestination
                ?.hierarchy
                ?.any { it.hasRoute(item.route::class) }
                ?: (item.route is MainNav.Home)

            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Box(
                    Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp)
                        )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 2.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {

                    val icon = if (isSelected) item.getActiveIcon() else item.getInactiveIcon()

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

sealed class NavigationItem(
    val route: Any,
    val getInactiveIcon: @Composable () -> ImageVector,
    val getActiveIcon: @Composable () -> ImageVector
) {
    object Home : NavigationItem(
        route = MainNav.Home,
        getInactiveIcon = { Icons.Default.Home },
        getActiveIcon = { Icons.Default.Home }
    )

    object Bookmarks : NavigationItem(
        route = MainNav.Bookmarks,
        getInactiveIcon = { ImageVector.vectorResource(R.drawable.bookmark_button_inactive) },
        getActiveIcon = { ImageVector.vectorResource(R.drawable.bookmark_button_active) }
    )
}
