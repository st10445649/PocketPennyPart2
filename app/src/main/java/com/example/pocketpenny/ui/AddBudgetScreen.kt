package com.example.pocketpenny.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.Budget
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.ExpenseDao
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBudgetScreen(
    navController: NavController,
    categories: List<Category>,
    dao: ExpenseDao
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var masterBudgetAmount by remember { mutableStateOf("") }
    val categoryMinBudgets = remember { mutableStateMapOf<Int?, String>() }
    val categoryMaxBudgets = remember { mutableStateMapOf<Int?, String>() }

    val totalPlanned = categoryMaxBudgets.values.sumOf { it.toDoubleOrNull() ?: 0.0 }
    val masterLimit = masterBudgetAmount.toDoubleOrNull() ?: 0.0
    val remaining = masterLimit - totalPlanned

    var errorMessage by remember { mutableStateOf("") }
    val currentMonthYear = remember {
        java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("MMMM yyyy"))
    }

    val progressValue = if (masterLimit > 0) (totalPlanned / masterLimit).toFloat() else 0f


    LaunchedEffect(Unit) {
        val existingBudgets = dao.getBudgetsForMonth(currentMonthYear)
        existingBudgets.forEach { budget ->
            if (budget.categoryId == -1) {
                masterBudgetAmount = budget.maxAmount.toString()
            } else {
                categoryMinBudgets[budget.categoryId] = budget.minAmount.toString()
                categoryMaxBudgets[budget.categoryId] = budget.maxAmount.toString()
            }
        }
    }

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )

    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(scrollState)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Add/Edit Budget",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1.2f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            //main container
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    //total/master budget
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    ) {
                        Text(
                            "Master Budget",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A5276),
                            modifier = Modifier.weight(1f)
                        )
                        BudgetInputField(
                            value = masterBudgetAmount,
                            onValueChange = { masterBudgetAmount = it },
                            placeholder = "R 10 000",
                            containerColor = Color(0xFFB3E5FC)
                        )
                    }

                    HorizontalDivider(color = Color(0xFFE1F5FE), thickness = 2.dp)
                    Spacer(modifier = Modifier.height(16.dp))

                    if (categories.isEmpty()) {
                        Text(
                            "No categories found. Add some when adding in expenses",
                            color = Color(0xFF1A5276),
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    categories.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {

                            Surface(
                                color = Color(category.color),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.weight(1f).height(50.dp)
                            ) {
                                Box(
                                    contentAlignment = Alignment.CenterStart,
                                    modifier = Modifier.padding(start = 16.dp)
                                ) {
                                    Text(
                                        text = category.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp

                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // space to add minimum amount per category
                            BudgetInputField(
                                value = categoryMinBudgets[category.id] ?: "",
                                onValueChange = {
                                    categoryMinBudgets[category.id] = it; errorMessage = ""
                                },
                                placeholder = "Min",
                                modifier = Modifier.width(70.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            // space to add maximum amount per category
                            BudgetInputField(
                                value = categoryMaxBudgets[category.id] ?: "",
                                onValueChange = {
                                    categoryMaxBudgets[category.id] = it; errorMessage = ""
                                },
                                placeholder = "Max",
                                modifier = Modifier.width(70.dp)
                            )
                        }
                    }
                    if (errorMessage.isNotEmpty()) {
                        Text(
                            errorMessage,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }


                    Spacer(modifier = Modifier.height(24.dp))
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Remaining budget to allocate: R$remaining",
                            color = if (remaining < 0) Color.Red else Color(0xFF1A5276),
                            fontWeight = FontWeight.Bold
                        )
                        LinearProgressIndicator(
                            progress = { progressValue.coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)),
                            color = if (remaining < 0) Color.Red else Color(0xFF4FC3F7),
                            trackColor = Color(0xFFE1F5FE)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            val masterLimit = masterBudgetAmount.toDoubleOrNull() ?: 0.0
                            val totalMaxPlanned =
                                categoryMaxBudgets.values.sumOf { it.toDoubleOrNull() ?: 0.0 }

                            if (totalMaxPlanned > masterLimit) {
                                errorMessage =
                                    "Total category budgets (R$totalMaxPlanned) exceed Master Budget (R$masterLimit)!"
                            } else {
                                scope.launch {
                                    // Save master / total budget with no category correlation
                                    dao.insertBudget(
                                        Budget(
                                            categoryId = -1,
                                            minAmount = 0.0,
                                            maxAmount = masterLimit,
                                            monthYear = currentMonthYear
                                        )
                                    )

                                    // Save Category budgets
                                    categories.forEach { cat ->
                                        val min =
                                            categoryMinBudgets[cat.id]?.toDoubleOrNull() ?: 0.0
                                        val max =
                                            categoryMaxBudgets[cat.id]?.toDoubleOrNull() ?: 0.0
                                        dao.insertBudget(
                                            Budget(
                                                categoryId = cat.id,
                                                minAmount = min,
                                                maxAmount = max,
                                                monthYear = currentMonthYear
                                            )
                                        )
                                    }
                                    navController.popBackStack()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
                    ) {
                        Text("Save All Budgets", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}


@Composable
fun BudgetInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color(0xFFF0F9FF)
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color(0xFF5C7A89)) },
        modifier = Modifier.width(120.dp).height(50.dp).clip(RoundedCornerShape(12.dp)),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = containerColor,
            unfocusedContainerColor = containerColor,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}


