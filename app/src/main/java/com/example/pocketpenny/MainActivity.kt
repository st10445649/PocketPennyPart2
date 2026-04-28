package com.example.pocketpenny

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import com.example.pocketpenny.data.AppDatabase
import com.example.pocketpenny.navigation.AppNavigation

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = AppDatabase.getDatabase(applicationContext)
        val userDao = database.userDao()
        val expenseDao = database.expenseDao()
        setContent {

                AppNavigation(userDao = userDao, expenseDao = expenseDao)

        }
    }
}



