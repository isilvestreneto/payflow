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
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
import com.payflow.app.ui.screens.cards.CardsScreen
import com.payflow.app.ui.screens.detail.*
import com.payflow.app.ui.screens.history.HistoryScreen
import com.payflow.app.ui.screens.home.HomeScreen
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeUiState
import com.payflow.app.ui.screens.profile.ProfileScreen
import com.payflow.app.ui.screens.settings.SettingsScreen

@Composable
fun PayFlowNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    currentThemeMode: AppThemeMode,
    onThemeModeChange: (AppThemeMode) -> Unit,
    currentCurrency: CurrencyPreference,
    onCurrencyChange: (CurrencyPreference) -> Unit,
    profilePhotoUri: String?,
    onProfilePhotoChange: (String?) -> Unit,
    onSignOut: () -> Unit
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
                val uiState by homeViewModel.uiState.collectAsState()
                
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToActiveSubscriptions = {
                        navController.navigate(Screen.ActiveSubscriptions.route)
                    },
                    onNavigateToMonthlySpending = {
                        navController.navigate(Screen.MonthlySpending.route)
                    },
                    onNavigateToAverageValue = {
                        navController.navigate(Screen.AverageValue.route)
                    },
                    onNavigateToMostExpensive = {
                        navController.navigate(Screen.MostExpensive.route)
                    },
                    onNavigateToCheapest = {
                        navController.navigate(Screen.Cheapest.route)
                    },
                    onNavigateToMostUsed = {
                        navController.navigate(Screen.MostUsed.route)
                    }
                )
            }
            
            composable(BottomNavItem.Cards.route) {
                CardsScreen(
                    onNavigateToCardDetail = {
                        // Navegar para detalhe do cartão
                    }
                )
            }
            
            composable(BottomNavItem.History.route) {
                HistoryScreen()
            }
            
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    currentThemeMode = currentThemeMode,
                    onThemeModeChange = onThemeModeChange,
                    currentCurrency = currentCurrency,
                    onCurrencyChange = onCurrencyChange,
                    profilePhotoUri = profilePhotoUri,
                    onProfilePhotoChange = onProfilePhotoChange,
                    onSignOut = onSignOut
                )
            }
            
            composable(BottomNavItem.Settings.route) {
                SettingsScreen()
            }
            
            // Detail Screens
            composable(Screen.ActiveSubscriptions.route) {
                val uiState by homeViewModel.uiState.collectAsState()
                val count = if (uiState is HomeUiState.Success) {
                    (uiState as HomeUiState.Success).summary.activeSubscriptionsCount
                } else 0
                
                ActiveSubscriptionsScreen(
                    count = count,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.MonthlySpending.route) {
                val uiState by homeViewModel.uiState.collectAsState()
                val spending = if (uiState is HomeUiState.Success) {
                    (uiState as HomeUiState.Success).summary.monthlySpending
                } else 0.0
                
                MonthlySpendingScreen(
                    totalSpending = spending,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.AverageValue.route) {
                val uiState by homeViewModel.uiState.collectAsState()
                val average = if (uiState is HomeUiState.Success) {
                    (uiState as HomeUiState.Success).summary.averageValue
                } else 0.0
                
                AverageValueScreen(
                    averageValue = average,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.MostExpensive.route) {
                val uiState by homeViewModel.uiState.collectAsState()
                val subscription = if (uiState is HomeUiState.Success) {
                    (uiState as HomeUiState.Success).summary.mostExpensive
                } else null
                
                MostExpensiveScreen(
                    subscription = subscription,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.Cheapest.route) {
                val uiState by homeViewModel.uiState.collectAsState()
                val subscription = if (uiState is HomeUiState.Success) {
                    (uiState as HomeUiState.Success).summary.cheapest
                } else null
                
                CheapestScreen(
                    subscription = subscription,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable(Screen.MostUsed.route) {
                val uiState by homeViewModel.uiState.collectAsState()
                val subscription = if (uiState is HomeUiState.Success) {
                    (uiState as HomeUiState.Success).summary.mostUsed
                } else null
                
                MostUsedScreen(
                    subscription = subscription,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
