package com.example.pocketpenny.ui

import android.R.attr.label
import android.R.attr.onClick
import android.R.attr.x
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
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
import com.example.pocketpenny.R
import com.example.pocketpenny.data.Expense
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import com.example.pocketpenny.data.ExpenseDao
import androidx.compose.runtime.*
import androidx.navigation.compose.currentBackStackEntryAsState


/*
Author: Gökhan Durmaz
Date Accessed: 27 April 2026
Link: https://medium.com/@gdurmaz1234/mastering-android-navigation-component-a-guide-to-navhost-and-navcontroller-d2df1bab09ef
Reason: Different methods of navigation using navhost and navcontroller. Helping to determine what is best for this system
*/
@Composable
fun BottomNavigationBar(navController: NavController, userId: Int) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    //home button
    NavigationBar(containerColor = Color(0xFF5CCAFF),
            modifier = Modifier.height(115.dp)) {
        val isSelected = currentRoute?.startsWith("home") == true

        NavigationBarItem(
            selected = isSelected,
            onClick = { navController.navigate("home/$userId") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home_nav),
                    contentDescription = "Home",
                    modifier = Modifier.size(45.dp),
                    tint = Color.Unspecified,

                )
            },
            label = { Text("Home", color = Color.White) },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White.copy(alpha = 0.3f) ,
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(0.7f)
            )
        )

        val isExpenseSelected = currentRoute?.startsWith("expenses") == true
        NavigationBarItem(
            selected = isExpenseSelected,
            onClick = { navController.navigate("expenses/$userId") },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.calcu_nav),
                    contentDescription = "Transactions",
                    modifier = Modifier.size(45.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Transactions", color = Color.White) },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White.copy(alpha = 0.3f) ,
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(0.7f)
            )
        )

        val isBudgetSelected = currentRoute?.startsWith("budget") == true
        NavigationBarItem(
            selected = isBudgetSelected,
            onClick = { navController.navigate("budget/$userId") },
            modifier = Modifier.offset(10.dp),
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.stats_nav),
                    contentDescription = "Budget",
                    modifier = Modifier.size(45.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Budget", color = Color.White) },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White.copy(alpha = 0.3f) ,
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(0.7f),
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* Penny Chatbot */ },
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.penny_nav),
                    contentDescription = "Penny",
                    modifier = Modifier.size(45.dp),
                    tint = Color.Unspecified
                )
            },
            label = { Text("Penny", color = Color.White) },

            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.White.copy(alpha = 0.3f) ,
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(0.7f)
            )
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