package com.example.pocketpenny.ui
import android.R.attr.padding
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.Expense
import com.example.pocketpenny.data.ExpenseDao
import kotlin.collections.component1
import kotlin.collections.component2
/*
Author: Backbase Design System
Date Accessed: 27 April 2026
Link: https://designsystem.backbase.com/latest/components/android/card/jetpack-compose-gwApSrRR
Reason: Documentation explaining different formats and styles for cards,
which is the most used component of the UI in the PennyPocket system.
*/

/*
Author: Victor Brandalise
Date Accessed: 27 April 2026
Link: https://victorbrandalise.com/budget-tracker-with-jetpack-compose/
Reason: Example budget tracker app showing an example what specifically the user needs to see first
*/

/*
Author: MyFixGuide
Date Accessed: 27 April 2026
Link: https://www.myfixguide.com/color-converter/
Reason: Colour converter to convert HEX codes from app colour palette to ARGB values used for UI
*/
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, expenseDao: ExpenseDao, userId: Int) {
    val expenses by expenseDao.getAllExpenses(userId).collectAsState(initial = emptyList())
    val budgets by expenseDao.getAllBudgets(userId).collectAsState(initial = emptyList())
    val categories by expenseDao.getAllCategories(userId).collectAsState(initial = emptyList())

    val currentMonth = java.time.LocalDate.now()
        .format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))


    val masterBudget =
        budgets.find { it.categoryId == -1 && it.monthYear == currentMonth }?.maxAmount ?: 1.0
    val totalSpent = expenses.sumOf { it.amount }

    val groupedExpenses = remember(expenses) {
        expenses.groupBy { expense ->
            java.time.Instant.ofEpochMilli(expense.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
                .format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM dd"))
        }
    }
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController,userId) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.White,
                shape = CircleShape,
                onClick = {
                    navController.navigate("add_expense/$userId")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expenses",
                    tint = Color(0xff00a9fc),
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .padding(padding)
        )
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
            Spacer(modifier = Modifier.height(50.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {Column {
                //header welcome section
                Text(
                    "Home",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    "Welcome Back!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    lineHeight = 32.sp
                )
            }
            //Text("Username", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(
                    onClick = {

                        navController.navigate("login") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            //section for budget
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 20.dp, bottomStart = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Monthly Budget",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A5276),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))


                        MultiColorProgressBar(expenses, categories, masterBudget)

                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                            Text(currentMonth, color = Color(0xFF5C7A89), fontSize = 12.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Text(
                                text = "R ${
                                    String.format(
                                        "%.2f",
                                        totalSpent
                                    )
                                } / R ${masterBudget.toInt()}",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A5276),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))

            //transactions section
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Recent Transactions",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xff00a9fc)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        LazyColumn {
                            groupedExpenses.forEach { (date, items) ->
                                item {
                                    Text(
                                        text = date,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A5276),
                                        modifier = Modifier.padding(vertical = 8.dp))
                                }

                                items(items) { expense ->
                                    TransactionItem(expense, expenseDao, userId)
                                }

                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }



