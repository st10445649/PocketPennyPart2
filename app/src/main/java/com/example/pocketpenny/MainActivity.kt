package com.example.pocketpenny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pocketpenny.data.AppDatabase
import com.example.pocketpenny.navigation.AppNavigation
import com.example.pocketpenny.ui.theme.PocketPennyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(applicationContext)
        val userDao = database.userDao()
        setContent {
            PocketPennyTheme {
                AppNavigation(userDao = userDao)
            }
        }
    }
}