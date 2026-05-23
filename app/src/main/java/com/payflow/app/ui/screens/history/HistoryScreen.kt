package com.payflow.app.ui.screens.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.ui.graphics.vector.ImageVector
import com.payflow.app.domain.model.SubscriptionStatus
import com.payflow.app.domain.model.Subscription

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToDetail: (subscriptionId: String) -> Unit,
    onNavigateToCreate: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: HistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Assinaturas") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar assinatura")
            }
        }
    ) { paddingValues ->
        when (uiState) {
            is HistoryUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            is HistoryUiState.Empty -> {
                EmptyState(
                    onCreateClick = onNavigateToCreate,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is HistoryUiState.NoResults -> {
                NoResultsContent(
                    state = uiState as HistoryUiState.NoResults,
                    onSortChange = { viewModel.changeSortOption(it) },
                    onStatusFilterChange = { viewModel.updateStatusFilter(it) },
                    onPeriodFilterChange = { start, end -> viewModel.updatePeriodFilter(start, end) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is HistoryUiState.Success -> {
                HistoryContent(
                    state = uiState as HistoryUiState.Success,
                    onNavigateToDetail = onNavigateToDetail,
                    onSortChange = { viewModel.changeSortOption(it) },
                    onStatusFilterChange = { viewModel.updateStatusFilter(it) },
                    onPeriodFilterChange = { start, end -> viewModel.updatePeriodFilter(start, end) },
                    modifier = Modifier.padding(paddingValues)
                )
            }

            is HistoryUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (uiState as HistoryUiState.Error).message,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(
    onCreateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ReceiptLong,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Nenhuma assinatura cadastrada",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Comece agora cadastrando sua primeira assinatura para controlar seus gastos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .padding(end = 8.dp)
            )
            Text("Criar Primeira Assinatura")
        }
    }
}

@Composable
private fun NoResultsContent(
    state: HistoryUiState.NoResults,
    onSortChange: (SortOption) -> Unit,
    onStatusFilterChange: (SubscriptionStatus?) -> Unit,
    onPeriodFilterChange: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton(
                icon = Icons.Default.Sort,
                label = "Ordenar",
                onClick = { showSortMenu = !showSortMenu },
                modifier = Modifier.weight(1f)
            )

            FilterButton(
                icon = Icons.Default.FilterList,
                label = "Filtrar",
                onClick = { showFilterMenu = !showFilterMenu },
                modifier = Modifier.weight(1f)
            )
        }

        if (showSortMenu) {
            SortMenu(
                currentSort = state.sortOption,
                onSortSelected = { option ->
                    onSortChange(option)
                    showSortMenu = false
                }
            )
        }

        if (showFilterMenu) {
            FilterMenu(
                currentStatus = state.statusFilter,
                onStatusSelected = { status ->
                    onStatusFilterChange(status)
                    showFilterMenu = false
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Nenhum resultado encontrado",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Ajuste os filtros para ver outras assinaturas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun HistoryContent(
    state: HistoryUiState.Success,
    onNavigateToDetail: (subscriptionId: String) -> Unit,
    onSortChange: (SortOption) -> Unit,
    onStatusFilterChange: (SubscriptionStatus?) -> Unit,
    onPeriodFilterChange: (LocalDate?, LocalDate?) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Filtros e Ordenação
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton(
                icon = Icons.Default.Sort,
                label = "Ordenar",
                onClick = { showSortMenu = !showSortMenu },
                modifier = Modifier.weight(1f)
            )

            FilterButton(
                icon = Icons.Default.FilterList,
                label = "Filtrar",
                onClick = { showFilterMenu = !showFilterMenu },
                modifier = Modifier.weight(1f)
            )
        }

        // Menu de Ordenação
        if (showSortMenu) {
            SortMenu(
                currentSort = state.sortOption,
                onSortSelected = { option ->
                    onSortChange(option)
                    showSortMenu = false
                }
            )
        }

        // Menu de Filtros
        if (showFilterMenu) {
            FilterMenu(
                currentStatus = state.statusFilter,
                onStatusSelected = { status ->
                    onStatusFilterChange(status)
                    showFilterMenu = false
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Lista de Assinaturas
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(
                items = state.subscriptions,
                key = { it.id }
            ) { subscription ->
                SubscriptionCard(
                    subscription = subscription,
                    onClick = { onNavigateToDetail(subscription.id.toString()) }
                )
            }
        }
    }
}

@Composable
private fun FilterButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(18.dp)
                .padding(end = 4.dp)
        )
        Text(label, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun SortMenu(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            SortMenuHeader()

            SortOption.values().forEach { option ->
                SortMenuItemButton(
                    option = option,
                    isSelected = option == currentSort,
                    onClick = { onSortSelected(option) }
                )
            }
        }
    }
}

@Composable
private fun SortMenuHeader() {
    Text(
        text = "Ordenar por",
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(16.dp)
    )
    Divider()
}

@Composable
private fun SortMenuItemButton(
    option: SortOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option.label,
            style = MaterialTheme.typography.bodyMedium
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun FilterMenu(
    currentStatus: SubscriptionStatus?,
    onStatusSelected: (SubscriptionStatus?) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Status",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(16.dp)
            )
            Divider()

            // Todos
            FilterMenuItemButton(
                label = "Todos",
                isSelected = currentStatus == null,
                onClick = { onStatusSelected(null) }
            )

            // Status específicos
            SubscriptionStatus.values().forEach { status ->
                FilterMenuItemButton(
                    label = status.displayName,
                    isSelected = status == currentStatus,
                    onClick = { onStatusSelected(status) }
                )
            }
        }
    }
}

@Composable
private fun FilterMenuItemButton(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun SubscriptionCard(
    subscription: Subscription,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (subscription.status) {
                SubscriptionStatus.ACTIVE -> MaterialTheme.colorScheme.primaryContainer
                SubscriptionStatus.PAUSED -> MaterialTheme.colorScheme.secondaryContainer
                SubscriptionStatus.CANCELED -> MaterialTheme.colorScheme.tertiaryContainer
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = subscription.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "R$ ${String.format("%.2f", subscription.value)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusBadge(status = subscription.status)
                    Text(
                        text = subscription.startDate.format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StatusBadge(
    status: SubscriptionStatus,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = when (status) {
            SubscriptionStatus.ACTIVE -> MaterialTheme.colorScheme.primary
            SubscriptionStatus.PAUSED -> MaterialTheme.colorScheme.secondary
            SubscriptionStatus.CANCELED -> MaterialTheme.colorScheme.error
        }
    ) {
        Text(
            text = status.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

