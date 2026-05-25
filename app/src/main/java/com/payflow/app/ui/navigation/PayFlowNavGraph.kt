package com.payflow.app.ui.navigation

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
import androidx.navigation.navArgument
import com.payflow.app.data.local.database.AppDatabase
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.repository.AuthRepository
import com.payflow.app.ui.screens.detail.*
import com.payflow.app.ui.screens.history.HistoryViewModelFactory
import com.payflow.app.ui.screens.home.HomeScreen
import com.payflow.app.ui.screens.home.HomeUiState
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeViewModelFactory
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import com.payflow.app.ui.screens.login.LoginScreen
import com.payflow.app.ui.screens.profile.ProfileScreen
import com.payflow.app.viewmodel.AuthViewModel
import com.payflow.app.ui.screens.historydetails.HistoryDetails
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.app.ui.screens.subscription.SubscriptionScreen
import com.payflow.app.ui.screens.subscription.SubscriptionViewModel
import com.payflow.app.ui.screens.subscription.SubscriptionViewModelFactory
import com.payflow.ui.screens.history.HistoryScreen
import com.payflow.ui.screens.history.HistoryViewModel
import androidx.credentials.CredentialManager
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch

@Composable
fun PayFlowNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    subscriptionRepository: SubscriptionRepository,
    userId: String,
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

    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(authState.usuario) {
        if (authState.usuario == null) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

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
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "home_$userId",
                    factory = factory
                )
                HomeScreen(
                    viewModel = homeViewModel,
                    user = authState.usuario,
                    onNavigateToProfile = {
                        navController.navigate(BottomNavItem.Profile.route) {
                            popUpTo(BottomNavItem.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToDetail = { subscriptionId ->
                        navController.navigate(Screen.HistoryDetails.createRoute(subscriptionId))
                    }
                )
            }

            // History
            composable(route = BottomNavItem.History.route) {
                val factory = HistoryViewModelFactory(GetSubscriptionsUseCase(subscriptionRepository), userId)
                val historyViewModel: HistoryViewModel = viewModel(
                    key = "history_$userId",
                    factory = factory
                )
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
                val authState by authViewModel.state.collectAsState()
                ProfileScreen(
                    usuario = authState.usuario,
                    currentThemeMode = currentThemeMode,
                    onThemeModeChange = onThemeModeChange,
                    currentCurrency = currentCurrency,
                    onCurrencyChange = onCurrencyChange,
                    profilePhotoUri = profilePhotoUri ?: authState.usuario?.fotoUrl,
                    onProfilePhotoChange = onProfilePhotoChange,
                    onSignOut = onSignOut
                )
            }

            // Detail Screens
            composable(Screen.ActiveSubscriptions.route) {
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "active_subs_$userId",
                    factory = factory
                )
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
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "monthly_spending_$userId",
                    factory = factory
                )
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
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "average_value_$userId",
                    factory = factory
                )
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
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "most_expensive_$userId",
                    factory = factory
                )
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
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "cheapest_$userId",
                    factory = factory
                )
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
                val factory = HomeViewModelFactory(
                    getHomeSummaryUseCase = GetHomeSummaryUseCase(
                        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
                    ),
                    userId = userId
                )
                val homeViewModel: HomeViewModel = viewModel(
                    key = "most_used_$userId",
                    factory = factory
                )
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
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val repository = remember { SubscriptionRepository(database.subscriptionDao()) }
                val scope = rememberCoroutineScope()

                val subscriptions by GetSubscriptionsUseCase(repository).invoke(userId).collectAsState(initial = emptyList())
                val subscriptionId = it.arguments?.getString("subscriptionId")
                val subscription = subscriptions.firstOrNull { sub -> sub.id == subscriptionId }

                if (subscription != null) {
                    HistoryDetails(
                        subscription = subscription,
                        onBackClick = { navController.popBackStack() },
                        onUseClick = { id ->
                            scope.launch {
                                repository.incrementUseCount(id)
                            }
                        }
                    )
                }
            }

            composable(Screen.Create.route) {
                val context = LocalContext.current
                val database = remember { AppDatabase.getDatabase(context) }
                val sharedPreferences =
                    remember { context.getSharedPreferences("payflow_prefs", Context.MODE_PRIVATE) }

                val subscriptionRepo =
                    remember { SubscriptionRepository(database.subscriptionDao()) }
                val authRepo = remember {
                    AuthRepository(
                        userDao = database.userDao(),
                        credentialManager = CredentialManager.create(context),
                        preferencias = sharedPreferences
                    )
                }

                val factory = remember { SubscriptionViewModelFactory(subscriptionRepo, authRepo) }
                val subscriptionViewModel: SubscriptionViewModel = viewModel(
                    key = "subscription_create_$userId",
                    factory = factory
                )

                SubscriptionScreen(
                    viewModel = subscriptionViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSaveSuccess = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}
