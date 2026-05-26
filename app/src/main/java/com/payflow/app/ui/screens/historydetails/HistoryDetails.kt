package com.payflow.app.ui.screens.historydetails

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.payflow.app.domain.model.Subscription
import com.payflow.app.domain.model.SubscriptionStatus
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetails(
    subscription: Subscription,
    onBackClick: () -> Unit,
    onUseClick: (String) -> Unit,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(bottom = 80.dp)
            )
        },
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Assinatura") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar assinatura",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            DetailsCard(subscription = subscription, useCount = subscription.useCount)
            DetailCard(subscription = subscription)
            if (!subscription.notes.isNullOrBlank()) {
                NotesCard(notes = subscription.notes)
            }

            Button(
                onClick = {
                    onUseClick(subscription.id)
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Uso registrado! Total: ${subscription.useCount + 1}x este mês",
                            duration = SnackbarDuration.Short
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Usei hoje")
            }
        }
    }
}

@Composable
private fun DetailsCard(
    subscription: Subscription,
    useCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (subscription.status) {
                SubscriptionStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                SubscriptionStatus.PAUSED -> MaterialTheme.colorScheme.secondaryContainer
                SubscriptionStatus.CANCELED -> MaterialTheme.colorScheme.tertiaryContainer
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = subscription.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "R$ ${String.format("%.2f", subscription.value)}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatusBadge(status = subscription.status)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Loop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Usado $useCount vez${if (useCount != 1) "es" else ""} este mês",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun DetailCard(subscription: Subscription) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Informações",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            HorizontalDivider()
            DetailRow(
                icon = Icons.Default.Category,
                label = "Categoria",
                value = subscription.type.displayName
            )
            DetailRow(
                icon = Icons.Default.CreditCard,
                label = "Forma de Pagamento",
                value = subscription.paymentMethod.displayName
            )
            DetailRow(
                icon = Icons.Default.EditCalendar,
                label = "Data de Início",
                value = subscription.startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            )
            DetailRow(
                icon = Icons.Default.CalendarMonth,
                label = "Dia de Cobrança",
                value = "${subscription.billingDate}º dia do mês"
            )
        }
    }
}

@Composable
private fun NotesCard(notes: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Notes,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Observações",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = notes,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DetailRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: SubscriptionStatus,
    modifier: Modifier = Modifier
) {
    val icon = when (status) {
        SubscriptionStatus.ACTIVE -> Icons.Default.CheckBox
        SubscriptionStatus.PAUSED -> Icons.Default.PauseCircle
        SubscriptionStatus.CANCELED -> Icons.Default.StopCircle
    }
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp),
            )
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.labelLarge,
            )
        }
}

@Preview(showBackground = true, name = "StatusBadge - Active")
@Composable
private fun StatusBadgeActivePreview() {
    StatusBadge(status = SubscriptionStatus.ACTIVE)
}

@Preview(showBackground = true, name = "StatusBadge - Paused")
@Composable
private fun StatusBadgePausedPreview() {
    StatusBadge(status = SubscriptionStatus.PAUSED)
}

@Preview(showBackground = true, name = "StatusBadge - Canceled")
@Composable
private fun StatusBadgeCanceledPreview() {
    StatusBadge(status = SubscriptionStatus.CANCELED)
}

@Preview(showBackground = true, name = "HistoryDetails Preview", showSystemUi = true)
@Composable
private fun HistoryDetailsPreview() {
    val subscription = Subscription(
        id = "1",
        name = "Netflix",
        value = 39.90,
        status = SubscriptionStatus.ACTIVE,
        type = com.payflow.app.domain.model.SubscriptionType.STREAMING,
        paymentMethod = com.payflow.app.domain.model.PaymentMethod.CREDIT_CARD,
        billingDate = 15,
        useCount = 3,
        notes = "Plano família compartilhado com 4 pessoas.",
        startDate = java.time.LocalDate.of(2023, 1, 15)
    )
    HistoryDetails(
        subscription = subscription,
        onBackClick = {},
        onUseClick = {},
        onEditClick = {}
    )
}
