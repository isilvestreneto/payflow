package com.payflow.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    data object Home : BottomNavItem(
        route = "home",
        icon = Icons.Default.Home,
        label = "Início"
    )
    
    data object Cards : BottomNavItem(
        route = "cards",
        icon = Icons.Default.CreditCard,
        label = "Cartões"
    )
    
    data object History : BottomNavItem(
        route = "history",
        icon = Icons.Default.History,
        label = "Histórico"
    )
    
    data object Profile : BottomNavItem(
        route = "profile",
        icon = Icons.Default.Person,
        label = "Perfil"
    )
    
    data object Settings : BottomNavItem(
        route = "settings",
        icon = Icons.Default.Settings,
        label = "Ajustes"
    )
    
    companion object {
        val items = listOf(Home, Cards, History, Profile, Settings)
    }
}
