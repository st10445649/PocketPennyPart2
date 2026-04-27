package com.example.pocketpenny.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.Expense
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(expense: Expense) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(12.dp).background(Color(0xFF64B5F6), CircleShape))

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(expense.description, fontWeight = FontWeight.Medium)
            Text("Category ID: ${expense.categoryId}", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFFE3F2FD)) {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate("home") },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("transactions") },
            icon = { Icon(Icons.Default.List, contentDescription = "Transactions") },
            label = { Text("List") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Penny Chatbot */ },
            icon = { Icon(Icons.Default.Person, contentDescription = "Penny") },
            label = { Text("Penny") }
        )
    }
}
@Composable
fun DateHeader(dateMillis: Long) {
    val formatter = SimpleDateFormat("EEEE, MMMM dd", Locale.getDefault())
    val dateString = formatter.format(Date(dateMillis))

    Text(
        text = dateString,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        style = MaterialTheme.typography.labelLarge,
        color = Color.Gray
    )
}



