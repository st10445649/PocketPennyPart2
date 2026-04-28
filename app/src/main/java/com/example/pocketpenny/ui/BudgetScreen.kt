package com.example.pocketpenny.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.Expense
import com.example.pocketpenny.data.ExpenseDao
import java.lang.ProcessBuilder.Redirect.to
import kotlin.collections.filter

@Composable
fun BudgetScreen(navController: NavController, dao: ExpenseDao) {
    val expenses by dao.getAllExpenses().collectAsState(initial = emptyList())
    val categories by dao.getAllCategories().collectAsState(initial = emptyList())
    val budgets by dao.getAllBudgets().collectAsState(initial = emptyList())

    val currentMonth = "April 2026"
    val masterBudget =
        budgets.find { it.categoryId == -1 && it.monthYear == currentMonth }?.maxAmount ?: 1.0
    val totalSpent = expenses.sumOf { it.amount }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                "Budgeting & Statistics",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(vertical = 20.dp).align(Alignment.CenterHorizontally)
            )


            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "Monthly Budget",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A5276),
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { navController.navigate("add_budget") }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Edit",
                                tint = Color(0xFF1A5276)
                            )
                        }
                    }

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
                            color = Color(0xFF1A5276)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Individual Category List
                    categories.forEach { category ->
                        val catBudget =
                            budgets.find { it.categoryId == category.id }?.maxAmount ?: 0.0
                        BudgetRow(category, catBudget)
                    }
                }
            }

            // Spending Stats Header
            Text(
                "Spending Stats",
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
                    .align(Alignment.CenterHorizontally),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color(0xFF1A5276)
            )

            // Placeholder for stats
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
               //todo: stats logic ... part 3
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(navController)
        }
    }
}

@Composable
fun BudgetRow(category: Category, amount: Double) {
    Surface(
        color = Color(category.color),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(50.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(category.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("R ${amount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MultiColorProgressBar(
    expenses: List<Expense>,
    categories: List<Category>,
    totalBudget: Double,
    modifier: Modifier = Modifier
) {
    // Calculate percentage used for each category
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            categories.forEach { category ->
                val categoryTotal = expenses.filter { it.categoryId == category.id }.sumOf { it.amount }
                val weight = (categoryTotal / totalBudget).toFloat()

                if (weight > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weight, fill = false)
                            .background(Color(category.color))
                    )
                }
            }
        }


        val totalSpent = expenses.sumOf { it.amount }
        val percentage = if (totalBudget > 0) (totalSpent / totalBudget * 100).toInt() else 0

        Text(
            text = "$percentage%",
            modifier = Modifier.align(Alignment.Center),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}