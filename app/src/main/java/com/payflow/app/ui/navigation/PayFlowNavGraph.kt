package com.payflow.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.payflow.app.ui.screens.history.HistoryScreen
import com.payflow.app.ui.screens.home.HomeScreen
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.profile.ProfileScreen
import com.payflow.app.ui.screens.settings.SettingsScreen

@Composable
fun PayFlowNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    // Verificar se deve mostrar bottom navigation
    val showBottomBar = currentDestination?.route in BottomNavItem.items.map { it.route }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { 
                            it.route == item.route 
                        } == true
                        
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(BottomNavItem.Home.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // Bottom Navigation Screens
            composable(BottomNavItem.Home.route) {
                HomeScreen(viewModel = homeViewModel)
            }
            
            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
            
            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
            }
            
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
