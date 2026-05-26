package com.payflow.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.core.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.IntOffset
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.roundToInt
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.payflow.app.domain.model.User
import java.text.NumberFormat
import java.time.LocalDate
import java.util.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    user: User?,
    profilePhotoUri: String?,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
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
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.onRetry() }
                        ) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }

            is HomeUiState.Success -> {
                if (state.summary.activeSubscriptionsCount == 0) {
                    HomeEmptyState(
                        user = user,
                        profilePhotoUri = profilePhotoUri,
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToCreate = onNavigateToCreate
                    )
                } else {
                    HomeContent(
                        summary = state.summary,
                        user = user,
                        profilePhotoUri = profilePhotoUri,
                        onNavigateToProfile = onNavigateToProfile,
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeContent(
    summary: com.payflow.app.domain.model.HomeSummary,
    user: User?,
    profilePhotoUri: String?,
    onNavigateToProfile: () -> Unit,
    onNavigateToDetail: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormat = remember {
        NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        // Saudação personalizada
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Olá, ${user?.nome?.split(" ")?.firstOrNull() ?: "Usuário"}! 👋",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                        .clickable(onClick = onNavigateToProfile),
                    contentAlignment = Alignment.Center
                ) {
                    val photoUrl = profilePhotoUri ?: user?.fotoUrl
                    if (photoUrl != null) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Foto de perfil",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Perfil",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Card principal - Total Mensal
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF6366F1), // Azul/Roxo
                                    Color(0xFF8B5CF6)  // Roxo
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Total Mensal:",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currencyFormat.format(summary.monthlySpending),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${summary.activeSubscriptionsCount} assinaturas ativas",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Duas colunas - Última Assinatura e Próximo Pagamento
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Última Assinatura (Mais Usada)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable(enabled = summary.mostUsed != null) {
                            summary.mostUsed?.let { onNavigateToDetail(it.id) }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE50914).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Mais\nUsada:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (summary.mostUsed != null) {
                            Text(
                                text = summary.mostUsed.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${summary.mostUsed.useCount} usos",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = "Nenhuma",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = Color(0xFFE50914),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Próximo Pagamento
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable(enabled = summary.nextDue != null) {
                            summary.nextDue?.let { onNavigateToDetail(it.id) }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF00A8E1).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Próximo\nPagamento:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (summary.nextDue != null) {
                            val today = LocalDate.now()
                            val currentDay = today.dayOfMonth
                            val billingDay = summary.nextDue.billingDate
                            val daysUntil = if (billingDay >= currentDay) {
                                billingDay - currentDay
                            } else {
                                val daysInMonth = today.lengthOfMonth()
                                (daysInMonth - currentDay) + billingDay
                            }
                            val dueText = when (daysUntil) {
                                0 -> "Hoje"
                                1 -> "Amanhã"
                                else -> "$daysUntil dias"
                            }

                            Text(
                                text = summary.nextDue.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "($dueText)",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = "Nenhuma",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color(0xFF00A8E1),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Duas colunas - Custo Mais Alto e Menos Usado
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Custo Mais Alto
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable(enabled = summary.mostExpensive != null) {
                            summary.mostExpensive?.let { onNavigateToDetail(it.id) }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF113CCF).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Custo\nMais Alto:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (summary.mostExpensive != null) {
                            Text(
                                text = summary.mostExpensive.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Text(
                                text = currencyFormat.format(summary.mostExpensive.value),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = "Nenhuma",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = Color(0xFF113CCF),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Menos Usado
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(140.dp)
                        .clickable(enabled = summary.leastUsed.firstOrNull() != null) {
                            summary.leastUsed.firstOrNull()?.let { onNavigateToDetail(it.id) }
                        },
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1DB954).copy(alpha = 0.15f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Menos\nUsado:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val leastUsedSub = summary.leastUsed.firstOrNull()
                        if (leastUsedSub != null) {
                            Text(
                                text = leastUsedSub.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${leastUsedSub.useCount} usos",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        } else {
                            Text(
                                text = "Nenhuma",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFF1DB954),
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Seção de Atenção - Assinaturas Pouco Usadas
        // Seção de Atenção - Assinaturas Pouco Usadas
        item {
            if (summary.leastUsed.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFC107).copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Atenção:",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Assinaturas pouco usadas este mês",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        summary.leastUsed.forEach { subscription ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                                    .clickable { onNavigateToDetail(subscription.id) },
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = subscription.name,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "Usado ${subscription.useCount}x este mês · ${currencyFormat.format(subscription.value)}/mês",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeEmptyState(
    user: User?,
    profilePhotoUri: String?,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreate: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
    ) {
        // Saudação no topo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Olá, ${user?.nome?.split(" ")?.firstOrNull() ?: "Usuário"}! 👋",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    .clickable(onClick = onNavigateToProfile),
                contentAlignment = Alignment.Center
            ) {
                val photoUrl = profilePhotoUri ?: user?.fotoUrl
                if (photoUrl != null) {
                    AsyncImage(
                        model = photoUrl,
                        contentDescription = "Foto de perfil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Perfil",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Card com ilustração
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Ilustração com ícones
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Ícones flutuantes ao redor
                    Box(
                        modifier = Modifier
                            .offset(x = (-60).dp, y = (-50).dp)
                            .size(70.dp)
                            .background(Color(0xFF6366F1), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Theaters,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 60.dp, y = (-40).dp)
                            .size(65.dp)
                            .background(Color(0xFF22D3EE), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudDownload,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = (-70).dp, y = 45.dp)
                            .size(68.dp)
                            .background(Color(0xFF0891B2), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .offset(x = 65.dp, y = 50.dp)
                            .size(72.dp)
                            .background(Color(0xFFFF9800), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Botão + central
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .background(Color.White, CircleShape)
                            .border(4.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                Text(
                    text = "Gerencie Suas\nAssinaturas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    lineHeight = 34.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subtexto
                Text(
                    text = "Controle seus gastos e organize seus\nserviços em um só lugar.",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botão de ação
        Button(
            onClick = onNavigateToCreate,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "NOVA ASSINATURA +",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun AnimatedSubscriptionIcons() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        modifier = Modifier
            .size(300.dp),
        contentAlignment = Alignment.Center
    ) {
        // Ícones ao redor
        val subscriptionServices = listOf(
            SubscriptionService("Netflix", Icons.Default.Movie, Color(0xFFE50914)),
            SubscriptionService("Spotify", Icons.Default.MusicNote, Color(0xFF1DB954)),
            SubscriptionService("YouTube", Icons.Default.PlayArrow, Color(0xFFFF0000)),
      
            SubscriptionService("HBO", Icons.Default.Tv, Color(0xFF8B5CF6))
        )

        subscriptionServices.forEachIndexed { index, service ->
            val angle = (index * 60f + rotation) * (Math.PI / 180f).toFloat()
            val radius = 110f
            val offsetX = radius * cos(angle.toDouble()).toFloat()
            val offsetY = radius * sin(angle.toDouble()).toFloat()

            Icon(
                imageVector = service.icon,
                contentDescription = service.name,
                tint = service.color,
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .size(42.dp)
                    .graphicsLayer {
                        alpha = 0.95f
                        rotationZ = -rotation
                    }
            )
        }

        // Botão + no centro
        Box(
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar",
                tint = Color(0xFF6366F1),
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

private data class SubscriptionService(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)

@Composable
private fun SuggestionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}