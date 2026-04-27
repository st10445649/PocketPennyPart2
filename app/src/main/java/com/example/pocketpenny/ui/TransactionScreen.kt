package com.example.pocketpenny.ui

import android.os.Build
import androidx.annotation.RequiresApi
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
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(navController: NavController, expenseDao: ExpenseDao) {

    val expenses by expenseDao.getAllExpenses().collectAsState(initial = emptyList())

    val groupedExpenses = remember(expenses) {
        expenses.groupBy { expense ->
            Instant.ofEpochMilli(expense.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd"))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transactions") },
                navigationIcon = {
                    IconButton(onClick = { /* filter later */ }) {
                        Icon(Icons.Default.List, contentDescription = "Filter")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_expense") }) {
                        Icon(Icons.Default.List, contentDescription = "Add")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {

            groupedExpenses.forEach { (date, itemsList) ->

                item {
                    Text(
                        text = date,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                }

                items(itemsList) { expense ->
                    TransactionItem(expense)
                }
            }
        }
    }
}
