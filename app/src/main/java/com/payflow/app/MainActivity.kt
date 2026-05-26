package com.payflow.app

import android.content.Context
import androidx.credentials.CredentialManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.payflow.app.data.local.database.AppDatabase
import com.payflow.app.data.local.repository.SubscriptionRepository
import com.payflow.app.data.local.repository.AuthRepository
import com.payflow.app.ui.navigation.PayFlowNavGraph
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
import com.payflow.app.ui.theme.PayFlowTheme
import com.payflow.app.viewmodel.AuthViewModel
import com.payflow.app.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PayFlowApp()
        }
    }
}

@Composable
fun PayFlowApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val sharedPreferences = remember { context.getSharedPreferences("payflow_prefs", Context.MODE_PRIVATE) }
    val subscriptionRepository = remember { SubscriptionRepository(database.subscriptionDao()) }

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            repository = AuthRepository(
                userDao = database.userDao(),
                credentialManager = CredentialManager.create(context),
                sharedPreferences
            )
        )
    )

    val authState by authViewModel.state.collectAsState()
    val userId = authState.usuario?.id ?: ""
    val currentThemeMode = authState.usuario?.themeMode ?: AppThemeMode.SYSTEM
    val currentCurrency = authState.usuario?.currency ?: CurrencyPreference.BRL
    var profilePhotoUri by remember { mutableStateOf<String?>(null) }

    val navController = rememberNavController()

    PayFlowTheme(themeMode = currentThemeMode) {
        PayFlowNavGraph(
            navController = navController,
            authViewModel = authViewModel,
            subscriptionRepository = subscriptionRepository,
            userId = userId,
            currentThemeMode = currentThemeMode,
            onThemeModeChange = { authViewModel.updateTheme(it) },
            currentCurrency = currentCurrency,
            onCurrencyChange = { authViewModel.updateCurrency(it) },
            profilePhotoUri = profilePhotoUri,
            onProfilePhotoChange = { profilePhotoUri = it },
            onSignOut = { authViewModel.logout() }
        )
    }
}