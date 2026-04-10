package com.app.teamsplitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.app.teamsplitter.ui.NavRoutes
import com.app.teamsplitter.ui.screens.history.HistoryScreen
import com.app.teamsplitter.ui.screens.players.PlayerEditScreen
import com.app.teamsplitter.ui.screens.players.PlayersScreen
import com.app.teamsplitter.ui.screens.session.SessionScreen
import com.app.teamsplitter.ui.theme.TeamSplitterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TeamSplitterTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Person, contentDescription = "Players") },
                    label = { Text("Players") },
                    selected = currentRoute == NavRoutes.PLAYERS,
                    onClick = {
                        navController.navigate(NavRoutes.PLAYERS) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = "Session") },
                    label = { Text("Session") },
                    selected = currentRoute == NavRoutes.SESSION,
                    onClick = {
                        navController.navigate(NavRoutes.SESSION) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.DateRange, contentDescription = "Story") },
                    label = { Text("Story") },
                    selected = currentRoute == NavRoutes.HISTORY,
                    onClick = {
                        navController.navigate(NavRoutes.HISTORY) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.PLAYERS,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.PLAYERS) {
                PlayersScreen(navController = navController)
            }
            composable(NavRoutes.SESSION) {
                SessionScreen(navController = navController)
            }
            composable(NavRoutes.PLAYER_ADD) {
                PlayerEditScreen(navController = navController, playerId = null)
            }
            composable(NavRoutes.PLAYER_EDIT) { backStackEntry ->
                val playerId = backStackEntry.arguments?.getString("playerId")?.toIntOrNull()
                PlayerEditScreen(navController = navController, playerId = playerId)
            }
            composable(NavRoutes.HISTORY) {
                HistoryScreen(navController = navController)
            }
        }
    }
}