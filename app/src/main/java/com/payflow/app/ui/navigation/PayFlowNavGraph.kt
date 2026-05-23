package com.payflow.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.payflow.app.ui.screens.detail.*
import com.payflow.app.ui.screens.history.HistoryViewModelFactory
import com.payflow.app.ui.screens.history.HistoryViewModelFactory
import com.payflow.app.ui.screens.home.HomeScreen
import com.payflow.app.ui.screens.home.HomeUiState
import com.payflow.app.ui.screens.home.HomeUiState
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.login.LoginScreen
import com.payflow.app.ui.screens.profile.ProfileScreen
import com.payflow.app.ui.screens.settings.SettingsScreen
import com.payflow.app.viewmodel.AuthViewModel
import com.payflow.app.ui.screens.history.HistoryScreen
import com.payflow.app.ui.screens.history.HistoryViewModel
import com.payflow.app.ui.screens.historydetails.HistoryDetails
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.ui.screens.history.HistoryScreen
import com.payflow.ui.screens.history.HistoryViewModel
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase

@Composable
fun PayFlowNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    getSubscriptionsUseCase: GetSubscriptionsUseCase,
    authViewModel: AuthViewModel,
    getSubscriptionsUseCase: GetSubscriptionsUseCase
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
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(paddingValues)
        ) {

            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.LOGIN) { inclusive = true } // remove login do stack
                        }
                    }
                )
            }

            // Bottom Navigation Screens
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel
                )
            }

            composable(route = BottomNavItem.History.route) {
                val factory = HistoryViewModelFactory(getSubscriptionsUseCase)
                val historyViewModel: HistoryViewModel = viewModel(factory = factory)
                HistoryScreen(
                    onNavigateToDetail = { subscriptionId ->
                        navController.navigate(Screen.HistoryDetails.createRoute(subscriptionId))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Screen.Create.route)
                    },
                    onBackClick = { navController.popBackStack() },
                    viewModel = historyViewModel
                )
            }

            composable(BottomNavItem.Profile.route) {
                ProfileScreen()
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
            
            composable(
                route = Screen.HistoryDetails.route,
                arguments = listOf(navArgument("subscriptionId") { type = NavType.StringType })
            ) {
                val subscriptions by getSubscriptionsUseCase().collectAsState(initial = emptyList())
                val subscriptionId = it.arguments?.getString("subscriptionId")
                val subscription = subscriptions.firstOrNull { sub -> sub.id.toString() == subscriptionId }
                
                if (subscription != null) {
                    HistoryDetails(
                        subscription = subscription,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
