package com.example.pocketpenny.ui

import android.R.attr.category
import android.R.attr.fontWeight
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.material3.SegmentedButtonDefaults.borderStroke
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontVariation.weight
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.Expense
import com.example.pocketpenny.data.ExpenseDao
import java.lang.ProcessBuilder.Redirect.to
import kotlin.collections.filter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetScreen(navController: NavController, dao: ExpenseDao) {
    val expenses by dao.getAllExpenses().collectAsState(initial = emptyList())
    val categories by dao.getAllCategories().collectAsState(initial = emptyList())
    val budgets by dao.getAllBudgets().collectAsState(initial = emptyList())

    val currentMonth = "April 2026"

    val monthlyExpenses = remember(expenses) {
        expenses.filter { expense ->
            val date = java.time.Instant.ofEpochMilli(expense.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            val format = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")
            date.format(format) == currentMonth
        }
    }

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

                    MultiColorProgressBar(monthlyExpenses, categories, masterBudget)

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
                            budgets.find { it.categoryId == category.id && it.monthYear == currentMonth}?.maxAmount ?: 0.0
                        val catSpent = monthlyExpenses.filter { it.categoryId == category.id }.sumOf { it.amount }
                        BudgetRow(category, catBudget,catSpent)
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
fun BudgetRow(category: Category, maxAmount: Double, spentAmount: Double) {
    val progress = if (maxAmount > 0) (spentAmount / maxAmount).toFloat().coerceIn(0f, 1f) else 0f
    val isOverBudget = spentAmount > maxAmount && maxAmount > 0
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon/Color
            Spacer(modifier = Modifier.width(8.dp))
            Text(category.name, fontWeight = FontWeight.Medium, color = Color(0xFF1A5276))
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "R ${spentAmount.toInt()} / R ${maxAmount.toInt()}",
                color = if (isOverBudget) Color.Red else Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        //changes border colour to red if budget is overspent
        val borderStroke = if (isOverBudget) 2.dp else 0.dp
        val borderColor = if (isOverBudget) Color.Red else Color.Transparent
        // Individual progress bar for the category
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(13.dp).clip(RoundedCornerShape(6.dp))
                .border(
                    width = borderStroke,
                    color = borderColor,
                    shape = RoundedCornerShape(6.dp)),
            color = Color(category.color),
            trackColor = Color(0xFFF0F9FF),

        )
    }
}
//    Surface(
//        color = Color(category.color),
//        shape = RoundedCornerShape(12.dp),
//        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).height(50.dp)
//    ) {
//        Row(
//            modifier = Modifier.padding(horizontal = 16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(category.name, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
//            Text("R ${amount.toInt()}", color = Color.White, fontWeight = FontWeight.Bold)
//        }
//    }
//}

@Composable
fun MultiColorProgressBar(
    expenses: List<Expense>,
    categories: List<Category>,
    totalBudget: Double,
    modifier: Modifier = Modifier
) {
    val totalSpent = expenses.sumOf { it.amount }
    val safeTotalBudget = if (totalBudget <= 0) 1.0 else totalBudget
    val totalPercentage = (totalSpent / safeTotalBudget).coerceIn(0.0, 1.0).toFloat()

    // Calculate percentage used for each category
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(color=Color(0xff54c7ff))
    ) {
        Row(modifier = Modifier.fillMaxWidth(totalPercentage).fillMaxSize()) {
            categories.forEach { category ->
                val categoryTotal = expenses.filter { it.categoryId == category.id }.sumOf { it.amount }

                if (categoryTotal > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight((categoryTotal / totalSpent).toFloat())
                            .background(Color(category.color))
                    )
                }
            }
        }


        Text(
            text = "${(totalPercentage * 100).toInt()}% Used",
            modifier = Modifier.align(Alignment.Center),
            color = if (totalPercentage > 0.5) Color.White else Color(0xFF1A5276),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}