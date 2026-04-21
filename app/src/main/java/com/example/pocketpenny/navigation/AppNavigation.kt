package com.example.pocketpenny.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pocketpenny.data.UserDao
import com.example.pocketpenny.ui.LoginScreen
import com.example.pocketpenny.ui.RegisterScreen

@Composable
fun AppNavigation(userDao: UserDao) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, userDao = userDao)
        }
        composable("register") {
            RegisterScreen(navController = navController, userDao = userDao)
        }
    }
}