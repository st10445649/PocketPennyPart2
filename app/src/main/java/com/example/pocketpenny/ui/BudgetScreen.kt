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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BudgetScreen(navController: NavController, dao: ExpenseDao, userId: Int) {
    val expenses by dao.getAllExpenses(userId).collectAsState(initial = emptyList())
    val categories by dao.getAllCategories(userId).collectAsState(initial = emptyList())
    val budgets by dao.getAllBudgets(userId).collectAsState(initial = emptyList())

    //automatic month generation
    val currentMonth = java.time.LocalDate.now()
        .format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))



    val monthlyExpenses = remember(expenses) {
        expenses.filter { expense ->
            val date = java.time.Instant.ofEpochMilli(expense.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            val format = java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy")
            date.format(format) == currentMonth
        }
    }

    //for filtering by user-selectable period
    val dateRangePickerState = rememberDateRangePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    val filteredExpenses = remember(expenses, dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        val start = dateRangePickerState.selectedStartDateMillis
        val end = dateRangePickerState.selectedEndDateMillis
        if (start != null && end != null) {
            expenses.filter { it.date in start..end }
        } else {
            monthlyExpenses // Default the stats to show current month if nothing is picked
        }
    }
    val resetFilter = {
        dateRangePickerState.setSelection(null, null)
    }

    val categoryTotals = remember(filteredExpenses) {
        filteredExpenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
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
                        IconButton(onClick = { navController.navigate("add_budget/$userId") }) {
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
            Spacer(modifier = Modifier.height(24.dp))
            Row(verticalAlignment = Alignment.CenterVertically,modifier = Modifier.fillMaxWidth().padding(top = 24.dp)
            ) {
                Text(
                    "Category Spending",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))

                // Filter Button
                TextButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Filter", fontSize= 18.sp,color = Color.White)
                }
            }
            // only shows if a date range has been selected
            if (dateRangePickerState.selectedStartDateMillis != null) {
                Surface(
                    color = Color.White.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        val start = java.time.Instant.ofEpochMilli(dateRangePickerState.selectedStartDateMillis!!)
                            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        val end = dateRangePickerState.selectedEndDateMillis?.let {
                            java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                        }

                        Text(
                            text = if (end != null) "$start - $end" else "From $start",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Spacer(Modifier.width(8.dp))

                        // The Reset Button
                        IconButton(
                            onClick = resetFilter,
                            modifier = Modifier.size(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Reset Filter",
                                tint = Color.White
                            )
                        }
                    }
                }
            }


            CategoryStatsCard(
                categoryTotals = categoryTotals,
                categories = categories
            )
            /*
            Author: Phillip Lackner
            Date Accessed: 27 April 2026
            Link: https://www.youtube.com/watch?v=BhyavkT2UO4
            Reason: Date picker example to be implemented in the expense creation feature.
            */
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = { TextButton(onClick = { showDatePicker = false }) { Text("OK") } }
                ) {
                    DateRangePicker(state = dateRangePickerState, modifier = Modifier.height(400.dp))
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(navController,userId)
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

//custom progress bar using calculations and weights of boxes to determine size and indicate visual progress
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

@Composable
fun CategoryStatsCard(
    categoryTotals: Map<Int, Double>,
    categories: List<Category>
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Spending by Category",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A5276),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            if (categoryTotals.isEmpty()) {
                Text("No spending in this period!", color = Color(0xFF1A5276), fontWeight = FontWeight.Bold)
                Text("Penny says: Keep chilling those expenses!", color = Color.Gray, fontSize = 12.sp)
            } else {
                // Display each category that has spending
                categoryTotals.forEach { (catId, total) ->
                    val category = categories.find { it.id == catId }
                    if (category != null) {
                        CategoryStatRow(category, total)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryStatRow(category: Category, total: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(12.dp)
                .background(Color(category.color), CircleShape)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = category.name,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1A5276),
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "R ${String.format("%.2f", total)}",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A5276),
            fontSize = 16.sp
        )
    }

    HorizontalDivider(color = Color(0xFFF0F9FF), thickness = 1.dp)
}


/*
Author: GeeksforGeeks
Date Accessed: 27 April 2026
Link: https://www.geeksforgeeks.org/android/card-in-android-jetpack-compose/
Reason: Card documentation and styling examples
*/
