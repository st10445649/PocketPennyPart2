package com.example.pocketpenny.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pocketpenny.data.ExpenseDao


@Composable
fun TransactionScreen(navController: NavController, expenseDao: ExpenseDao) {
    val expenses by expenseDao.getAllExpenses().collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.List, contentDescription = "Filter")
            Text("All Transactions", style = MaterialTheme.typography.titleLarge)
        }

        LazyColumn {
            val grouped = expenses.groupBy { it.date }
            grouped.forEach { (date, dayExpenses) ->
                item { DateHeader(date) }
                items(dayExpenses) { expense ->
                    TransactionItem(expense)
                }
            }
        }
    }
}