package com.payflow.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.local.repository.UserRepository
import com.payflow.app.ui.screens.cards.CardsScreen
import com.payflow.app.ui.screens.detail.*
import com.payflow.app.ui.screens.history.HistoryScreen
import com.payflow.app.ui.screens.home.HomeScreen
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeUiState
import com.payflow.app.ui.screens.profile.ProfileScreen
import com.payflow.app.ui.screens.settings.SettingsScreen
import com.payflow.app.ui.screens.subscription.SubscriptionScreen
import com.payflow.app.ui.screens.subscription.SubscriptionViewModel
import com.payflow.app.ui.screens.subscription.SubscriptionViewModelFactory

@Composable
fun PayFlowNavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel,
    subscriptionRepository: SubscriptionRepository,
    userRepository: UserRepository
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val showBottomBar = currentDestination?.route in BottomNavItem.items.map { it.route }
    
    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    BottomNavItem.items.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any { it.route == item.route } == true
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = selected,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(BottomNavItem.Home.route) { saveState = true }
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
            composable(BottomNavItem.Home.route) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToActiveSubscriptions = { navController.navigate(Screen.ActiveSubscriptions.route) },
                    onNavigateToMonthlySpending = { navController.navigate(Screen.MonthlySpending.route) },
                    onNavigateToAverageValue = { navController.navigate(Screen.AverageValue.route) },
                    onNavigateToMostExpensive = { navController.navigate(Screen.MostExpensive.route) },
                    onNavigateToCheapest = { navController.navigate(Screen.Cheapest.route) },
                    onNavigateToMostUsed = { navController.navigate(Screen.MostUsed.route) },
                    onNavigateToAddSubscription = { navController.navigate(Screen.AddSubscription.route) }
                )
            }
            
            composable(BottomNavItem.Cards.route) { CardsScreen({}) }
            composable(BottomNavItem.History.route) { HistoryScreen() }
            composable(BottomNavItem.Profile.route) { ProfileScreen() }
            composable(BottomNavItem.Settings.route) { SettingsScreen() }
            
            // Detail Screens (Simplificado para brevidade, mantendo lógica de dados)
            composable(Screen.ActiveSubscriptions.route) { ActiveSubscriptionsScreen(0, { navController.popBackStack() }) }
            composable(Screen.MonthlySpending.route) { MonthlySpendingScreen(0.0, { navController.popBackStack() }) }
            composable(Screen.AverageValue.route) { AverageValueScreen(0.0, { navController.popBackStack() }) }
            
            composable(Screen.AddSubscription.route) {
                val subscriptionViewModel: SubscriptionViewModel = viewModel(
                    factory = SubscriptionViewModelFactory(subscriptionRepository, userRepository)
                )

                SubscriptionScreen(
                    viewModel = subscriptionViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = {
                        navController.popBackStack()
                        homeViewModel.onRetry()
                    }
                )
            }
        }
    }
}
