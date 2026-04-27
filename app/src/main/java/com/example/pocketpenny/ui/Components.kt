package com.example.pocketpenny.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.indicatorColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.example.pocketpenny.R
import com.example.pocketpenny.data.Expense
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import com.example.pocketpenny.data.ExpenseDao
import androidx.compose.runtime.*

@Composable
fun TransactionItem(expense: Expense, dao: ExpenseDao? = null) {
    var category by remember { mutableStateOf<com.example.pocketpenny.data.Category?>(null) }

    LaunchedEffect(expense.categoryId) {
        category = dao?.getCategoryById(expense.categoryId)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = Color(category?.color ?: Color.Gray.toArgb()),
                    shape = CircleShape
                )
        )

        Spacer(Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = expense.title,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF1A5276)
            )
            Text(
                text = category?.name ?: "Unknown Category",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        Text(
            text = "R ${expense.amount}",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A5276)
        )
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    NavigationBar(containerColor = Color(0xFF5CCAFF)) {
        NavigationBarItem(
            selected = true,
            onClick = { navController.navigate("home") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home_nav),contentDescription = "Home",
                   modifier = Modifier.size(45.dp), tint = Color.Unspecified)},
            label = { Text("Home", color= Color.White) },
                    colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White.copy(alpha = 0.3f)
                    )
        )
        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("expenses") },
            icon = { Icon(painter = painterResource(id = R.drawable.calc_nav), contentDescription = "Transactions",
                modifier = Modifier.size(45.dp), tint = Color.Unspecified) },
            label = { Text("List", color= Color.White)}
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Penny Chatbot */ },
            icon = { Icon(painter = painterResource(id = R.drawable.penny_nav), contentDescription = "Penny",
                modifier = Modifier.size(45.dp), tint = Color.Unspecified) },
            label = { Text("Penny", color= Color.White) }
        )
    }
}
@Composable
fun DateHeader(dateMillis: Long) {
    val formatter = SimpleDateFormat("EEEE, MMMM dd", LocalLocale.current.platformLocale)
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



