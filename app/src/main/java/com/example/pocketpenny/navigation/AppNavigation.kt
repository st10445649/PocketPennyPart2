package com.example.pocketpenny.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketpenny.data.UserDao
import com.example.pocketpenny.data.ExpenseDao
import com.example.pocketpenny.ui.*

@Composable
fun AppNavigation(userDao: UserDao, expenseDao: ExpenseDao) {
    val navController = rememberNavController()
    val categories by expenseDao.getAllCategories().collectAsState(initial = emptyList())

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, userDao = userDao)
        }
        composable("register") {
            RegisterScreen(navController = navController, userDao = userDao)
        }
        composable("home") {
            HomeScreen(navController = navController, expenseDao = expenseDao)
        }
        composable("transactions") {
            TransactionScreen(navController = navController, expenseDao = expenseDao)
        }
        composable("add_expense") {
            AddExpenseScreen(expenseDao, categories)
        }
    }
}