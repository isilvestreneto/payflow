package com.payflow.app.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payflow.app.ui.components.AnimatedCreditCard
import com.payflow.app.ui.screens.home.components.HighlightCard
import com.payflow.app.ui.screens.home.components.SummaryCard
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToActiveSubscriptions: () -> Unit,
    onNavigateToMonthlySpending: () -> Unit,
    onNavigateToAverageValue: () -> Unit,
    onNavigateToMostExpensive: () -> Unit,
    onNavigateToCheapest: () -> Unit,
    onNavigateToMostUsed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "PayFlow",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { /* Navegar para perfil */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.onRetry() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            
            is HomeUiState.Success -> {
                HomeContent(
                    summary = state.summary,
                    onNavigateToActiveSubscriptions = onNavigateToActiveSubscriptions,
                    onNavigateToMonthlySpending = onNavigateToMonthlySpending,
                    onNavigateToAverageValue = onNavigateToAverageValue,
                    onNavigateToMostExpensive = onNavigateToMostExpensive,
                    onNavigateToCheapest = onNavigateToCheapest,
                    onNavigateToMostUsed = onNavigateToMostUsed,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    summary: com.payflow.app.domain.model.HomeSummary,
    onNavigateToActiveSubscriptions: () -> Unit,
    onNavigateToMonthlySpending: () -> Unit,
    onNavigateToAverageValue: () -> Unit,
    onNavigateToMostExpensive: () -> Unit,
    onNavigateToCheapest: () -> Unit,
    onNavigateToMostUsed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }
    
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        // Título dos cartões
        item {
            Text(
                text = "Meus Cartões",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Card de cartão animado
        item {
            AnimatedCreditCard(
                cardNumber = "**** **** **** 3346",
                cardHolder = "Cholrul Syafril",
                balance = currencyFormat.format(summary.monthlySpending),
                cardColor = listOf(
                    Color(0xFF0F4C75),
                    Color(0xFF1B262C)
                ),
                onClick = { /* Abrir detalhes do cartão */ }
            )
        }
        
        // Card de destaque - Gasto Mensal
        item {
            HighlightCard(
                title = "Gasto Mensal Total",
                mainValue = currencyFormat.format(summary.monthlySpending),
                subtitle = "Soma de todas as assinaturas ativas",
                icon = Icons.Default.AttachMoney,
                onClick = onNavigateToMonthlySpending
            )
        }
        
        // Título das métricas
        item {
            Text(
                text = "Suas Métricas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        // Card - Assinaturas Ativas
        item {
            SummaryCard(
                title = "Assinaturas Ativas",
                value = "${summary.activeSubscriptionsCount}",
                icon = Icons.Default.Subscriptions,
                subtitle = "Total de serviços ativos",
                onClick = onNavigateToActiveSubscriptions
            )
        }
        
        // Card - Média de Valor
        item {
            SummaryCard(
                title = "Valor Médio",
                value = currencyFormat.format(summary.averageValue),
                icon = Icons.Default.TrendingUp,
                subtitle = "Média por assinatura",
                onClick = onNavigateToAverageValue
            )
        }
        
        // Título dos destaques
        item {
            Text(
                text = "Destaques",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        // Card - Mais Cara
        item {
            SummaryCard(
                title = "Assinatura Mais Cara",
                value = summary.mostExpensive?.let { currencyFormat.format(it.value) } ?: "N/A",
                icon = Icons.Default.ArrowUpward,
                subtitle = summary.mostExpensive?.name ?: "Nenhuma assinatura",
                onClick = onNavigateToMostExpensive
            )
        }
        
        // Card - Mais Barata
        item {
            SummaryCard(
                title = "Assinatura Mais Barata",
                value = summary.cheapest?.let { currencyFormat.format(it.value) } ?: "N/A",
                icon = Icons.Default.ArrowDownward,
                subtitle = summary.cheapest?.name ?: "Nenhuma assinatura",
                onClick = onNavigateToCheapest
            )
        }
        
        // Card - Mais Usada
        item {
            SummaryCard(
                title = "Assinatura Mais Usada",
                value = "${summary.mostUsed?.useCount ?: 0} usos",
                icon = Icons.Default.Star,
                subtitle = summary.mostUsed?.name ?: "Nenhuma assinatura",
                onClick = onNavigateToMostUsed
            )
        }
    }
}
