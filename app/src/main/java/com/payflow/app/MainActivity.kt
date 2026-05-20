package com.payflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
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
fun PayFlowApp(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    
    // TODO: Substituir por injeção de dependência com Hilt quando configurado
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
        getSubscriptionsUseCase = GetSubscriptionsUseCase()
    )
}

@Preview(showBackground = true)
@Composable
fun PayFlowAppPreview() {
    PayFlowTheme {
        PayFlowApp()
    }
}