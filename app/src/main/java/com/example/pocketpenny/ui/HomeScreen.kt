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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeScreen(navController: NavController, expenseDao: ExpenseDao) {
    val expenses by expenseDao.getAllExpenses().collectAsState(initial = emptyList())

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
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                containerColor = Color.White,
                shape = CircleShape,
                onClick = {
                    navController.navigate("add_expense")
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Expense",
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
                color = Color.White,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(30.dp))
            Card(
                modifier = Modifier.fillMaxSize(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Recent Transactions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color(0xFF1A5276)
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
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                            items(items) { expense ->
                                TransactionItem(expense, expenseDao)
                            }

                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TransactionItem(expense: Expense, dao: ExpenseDao) {

        var category by remember { mutableStateOf<Category?>(null) }


        LaunchedEffect(expense.categoryId) {
            category = dao.getCategoryById(expense.categoryId)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(Color(category?.color ?: Color.Gray.toArgb()), CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(expense.title, fontWeight = FontWeight.Medium, color = Color(0xFF1A5276))
                // Only show image/link if there is an image attached
                if (expense.filePath != null) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }


            Text(
                "R ${expense.amount}",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A5276)
            )
        }
    }
}
