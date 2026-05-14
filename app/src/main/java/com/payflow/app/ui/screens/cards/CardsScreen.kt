package com.payflow.app.ui.screens.cards

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardsScreen(
    onNavigateToCardDetail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Meus Cartões",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Adicionar cartão */ }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar cartão"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Seus cartões cadastrados",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Cards serão adicionados aqui
            com.payflow.app.ui.components.AnimatedCreditCard(
                cardNumber = "**** **** **** 3346",
                cardHolder = "Cholrul Syafril",
                balance = "R$ 1.400.512,31",
                cardColor = listOf(
                    Color(0xFF0F4C75),
                    Color(0xFF1B262C)
                ),
                onClick = onNavigateToCardDetail
            )
            
            com.payflow.app.ui.components.AnimatedCreditCard(
                cardNumber = "**** **** **** 7821",
                cardHolder = "Cholrul Syafril",
                balance = "R$ 87.234,50",
                cardColor = listOf(
                    Color(0xFF6C63FF),
                    Color(0xFF4A47A3)
                ),
                onClick = onNavigateToCardDetail
            )
        }
    }
}
