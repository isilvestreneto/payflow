package com.payflow.app

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.payflow.app.data.local.database.AppDatabase
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.repository.AuthRepository
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.app.ui.navigation.PayFlowNavGraph
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeViewModelFactory
import com.payflow.app.viewmodel.AuthViewModel
import com.payflow.app.viewmodel.AuthViewModelFactory
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MainActivity", "========================================")
        Log.e("MainActivity", "APP INICIADO - onCreate")
        Log.e("MainActivity", "========================================")
        enableEdgeToEdge()
        setContent {
            PayFlowApp()
        }
    }
}

@Composable
fun PayFlowApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val subscriptionRepository = remember { SubscriptionRepository(database.subscriptionDao()) }
    //    val userRepository = remember { UserRepository(database.userDao()) }
    val sharedPreferences = context.getSharedPreferences("payflow_prefs", Context.MODE_PRIVATE)
    var currentThemeMode by remember { mutableStateOf(AppThemeMode.SYSTEM) }
    var currentCurrency by remember { mutableStateOf(CurrencyPreference.BRL) }
    var profilePhotoUri by remember { mutableStateOf<String?>(null) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            repository = AuthRepository(
                userDao = database.userDao(),
                credentialManager = CredentialManager.create(context),
                sharedPreferences
            )
        )
    )

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
        authViewModel = authViewModel,
        getSubscriptionsUseCase = GetSubscriptionsUseCase(subscriptionRepository),
        currentThemeMode = currentThemeMode,
        onThemeModeChange = { currentThemeMode = it },
        currentCurrency = currentCurrency,
        onCurrencyChange = { currentCurrency = it },
        profilePhotoUri = profilePhotoUri,
        onProfilePhotoChange = { profilePhotoUri = it },
        onSignOut = {
            (context as? Activity)?.finishAffinity()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun PayFlowAppPreview() {
    PayFlowApp()
}
