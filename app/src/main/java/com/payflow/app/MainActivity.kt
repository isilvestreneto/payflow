package com.payflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.payflow.app.data.local.database.AppDatabase
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.local.repository.UserRepository
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.app.ui.navigation.PayFlowNavGraph
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeViewModelFactory
import com.payflow.app.ui.theme.PayFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PayFlowTheme {
                PayFlowApp()
            }
        }
    }
}

@Composable
fun PayFlowApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Incializa as dependências de forma segura (apenas uma vez) usando remember
    val database = remember { AppDatabase.getDatabase(context) }
    val subscriptionRepository = remember { SubscriptionRepository(database.subscriptionDao()) }
    val userRepository = remember { UserRepository(database.userDao()) }
    
    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            getHomeSummaryUseCase = GetHomeSummaryUseCase(
                getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository)
            )
        )
    )

    PayFlowNavGraph(
        navController = navController,
         homeViewModel = homeViewModel,
        subscriptionRepository = subscriptionRepository,
        userRepository = userRepository
    )
}
