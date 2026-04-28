package com.example.pocketpenny.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketpenny.data.UserDao
import com.example.pocketpenny.data.ExpenseDao
import com.example.pocketpenny.ui.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(userDao: UserDao, expenseDao: ExpenseDao) {
    val navController = rememberNavController()
    val categories by expenseDao.getAllCategories().collectAsState(initial = emptyList())

    NavHost(navController = navController, startDestination = "home") {
        composable("login") {
            LoginScreen(navController = navController, userDao = userDao)
        }
        composable("register") {
            RegisterScreen(navController = navController, userDao = userDao)
        }
        composable("home") {
            HomeScreen(navController = navController, expenseDao = expenseDao)
        }
        composable("expenses") {
            TransactionScreen(navController = navController, expenseDao = expenseDao)
        }
        composable("add_expense") {
            AddExpenseScreen(navController = navController,dao= expenseDao, categories)
        }
    }
}