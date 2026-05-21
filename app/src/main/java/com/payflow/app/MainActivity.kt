package com.payflow.app

import android.content.Context
import android.content.SharedPreferences
import androidx.credentials.CredentialManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.payflow.app.data.repository.AppDatabase
import com.payflow.app.data.repository.AuthRepository
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.app.ui.navigation.PayFlowNavGraph
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeViewModelFactory
import com.payflow.app.ui.theme.PayFlowTheme
import com.payflow.app.viewmodel.AuthViewModel
import com.payflow.app.viewmodel.AuthViewModelFactory

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
fun PayFlowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("payflow_prefs", Context.MODE_PRIVATE)

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(
            repository = AuthRepository(
                userDao = AppDatabase.get(context).userDao(),
                credentialManager = CredentialManager.create(context),
                sharedPreferences
            )
        )
    )

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            getHomeSummaryUseCase = GetHomeSummaryUseCase(
                getSubscriptionsUseCase = GetSubscriptionsUseCase()
            )
        )
    )

    PayFlowNavGraph(
        navController = navController,
        homeViewModel = homeViewModel,
        authViewModel = authViewModel
    )
}

@Preview(showBackground = true)
@Composable
fun PayFlowAppPreview() {
    PayFlowTheme {
        PayFlowApp()
    }
}