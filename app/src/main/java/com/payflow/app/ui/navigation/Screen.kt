package com.payflow.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ActiveSubscriptions : Screen("active_subscriptions")
    data object MonthlySpending : Screen("monthly_spending")
    data object AverageValue : Screen("average_value")
    data object MostExpensive : Screen("most_expensive")
    data object Cheapest : Screen("cheapest")
    data object MostUsed : Screen("most_used")
    data object AddSubscription : Screen("add_subscription")
    data object HistoryDetails : Screen("history_details/{subscriptionId}") {
        fun createRoute(subscriptionId: String) = "history_details/$subscriptionId"
    }
    data object Create : Screen("create")
    data object Detail : Screen("detail") {
        fun createRoute(subscriptionId: String) {}
    }
}
