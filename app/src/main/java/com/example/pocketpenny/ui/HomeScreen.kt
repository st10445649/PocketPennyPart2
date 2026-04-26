package com.example.pocketpenny.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pocketpenny.data.ExpenseDao

@Composable
fun HomeScreen(navController: NavController, expenseDao: ExpenseDao) {
    val expenses by expenseDao.getAllExpenses().collectAsState(initial = emptyList())

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* Navigate to Add Expense */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium)

            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Monthly Budget: Mar 2026")
                    LinearProgressIndicator(
                        progress = 0.75f,
                        modifier = Modifier.fillMaxWidth().height(8.dp)
                    )
                    Text("R4 189.94 / R5 600", modifier = Modifier.align(Alignment.End))
                }
            }

            Text("Recent Transactions", fontWeight = FontWeight.Bold)
            LazyColumn {
                items(expenses.take(5)) { expense ->
                    TransactionItem(expense)
                }
            }
        }
    }
}