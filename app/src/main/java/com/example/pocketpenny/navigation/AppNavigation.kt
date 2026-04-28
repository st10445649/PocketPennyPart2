package com.example.pocketpenny.navigation

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketpenny.data.UserDao
import com.example.pocketpenny.data.ExpenseDao
import com.example.pocketpenny.ui.*
import androidx.compose.runtime.setValue

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(userDao: UserDao, expenseDao: ExpenseDao) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login" ){
        composable("login") {
            LoginScreen(navController = navController, userDao = userDao)
        }

        //REGISTER
        composable("register") {
            RegisterScreen(navController = navController, userDao = userDao)
        }

        //HOME
        composable("home/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
            HomeScreen(navController, expenseDao, userId)
        }

        //TRANSACTION SCREEN
        composable("expenses/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
            TransactionScreen(navController = navController, expenseDao = expenseDao, userId = userId)
        }

        // ADD EXPENSE Screen
        composable("add_expense/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
            val categories by expenseDao.getAllCategories(userId).collectAsState(initial = emptyList())
            AddExpenseScreen(navController = navController, dao = expenseDao, categories = categories, userId = userId)
        }

        // BUDGET MAIN SCREEN
        composable("budget/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
            BudgetScreen(navController = navController, dao = expenseDao, userId = userId)
        }

        // ADD BUDGET
        composable("add_budget/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")?.toInt() ?: -1
            val categories by expenseDao.getAllCategories(userId).collectAsState(initial = emptyList())
            AddBudgetScreen(navController = navController, categories = categories, dao = expenseDao, userId = userId)
        }

    }
}