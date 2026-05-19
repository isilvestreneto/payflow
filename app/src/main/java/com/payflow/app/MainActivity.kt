package com.payflow.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.payflow.app.domain.usecase.GetHomeSummaryUseCase
import com.payflow.app.domain.usecase.GetSubscriptionsUseCase
import com.payflow.app.ui.navigation.PayFlowNavGraph
import com.payflow.app.ui.preferences.AppThemeMode
import com.payflow.app.ui.preferences.CurrencyPreference
import com.payflow.app.ui.screens.home.HomeViewModel
import com.payflow.app.ui.screens.home.HomeViewModelFactory
import com.payflow.app.ui.theme.PayFlowTheme

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
fun PayFlowApp(modifier: Modifier = Modifier) {
    var currentThemeMode by remember { mutableStateOf(AppThemeMode.SYSTEM) }
    var currentCurrency by remember { mutableStateOf(CurrencyPreference.BRL) }
    var profilePhotoUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    PayFlowTheme(themeMode = currentThemeMode) {
        val navController = rememberNavController()

        // TODO: Substituir por injecao de dependencia com Hilt quando configurado
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
}

@Preview(showBackground = true)
@Composable
fun PayFlowAppPreview() {
    PayFlowApp()
}
