package com.example.pocketpenny.ui

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pocketpenny.data.ExpenseDao
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(navController: NavController, expenseDao: ExpenseDao) {
    // Data States
    val expenses by expenseDao.getAllExpenses().collectAsState(initial = emptyList())
    val categories by expenseDao.getAllCategories().collectAsState(initial = emptyList())

    // Filter Logic States
    val selectedFilterCategories = remember { mutableStateListOf<Int>() }
    var sliderPosition by remember { mutableStateOf(0f..5000f) }
    val dateRangePickerState = rememberDateRangePickerState()

    // UI Visibility States
    var showDatePicker by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    // THE FILTER ENGINE
    val filteredExpenses = remember(expenses, selectedFilterCategories.size, sliderPosition, dateRangePickerState.selectedStartDateMillis, dateRangePickerState.selectedEndDateMillis) {
        expenses.filter { expense ->
            val matchesCategory = selectedFilterCategories.isEmpty() || selectedFilterCategories.contains(expense.categoryId)
            val matchesAmount = expense.amount >= sliderPosition.start && expense.amount <= sliderPosition.endInclusive
            val matchesDate = if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
                expense.date >= dateRangePickerState.selectedStartDateMillis!! &&
                        expense.date <= dateRangePickerState.selectedEndDateMillis!!
            } else true

            matchesCategory && matchesAmount && matchesDate
        }
    }

    val groupedExpenses = remember(filteredExpenses) {
        filteredExpenses.groupBy { expense ->
            Instant.ofEpochMilli(expense.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
                .format(DateTimeFormatter.ofPattern("EEEE, MMMM dd"))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Transactions", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { showFilterSheet = true }) {
                        Icon(imageVector = Icons.Default.FilterList, contentDescription = "Filter", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("add_expense") }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color(0xff00a9fc))
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xffddf4ff))) {
            LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                groupedExpenses.forEach { (date, items) ->
                    item {
                        Text(
                            text = date,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A5276),
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                    items(items) { expense ->
                        TransactionItem(expense, expenseDao)
                    }
                }
            }
        }
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = { showDatePicker = false }) { Text("OK") }
                }
            ) {
                DateRangePicker(
                    state = dateRangePickerState,
                    title = { Text("Select Date Range", modifier = Modifier.padding(16.dp)) },
                    modifier = Modifier.height(450.dp)
                )
            }
        }
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                containerColor = Color(0xFF64B5F6),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showFilterSheet = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Apply Filters", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            // --- NEW RESET BUTTON ---
                            TextButton(onClick = {
                                selectedFilterCategories.clear()
                                sliderPosition = 0f..5000f
                                dateRangePickerState.setSelection(null, null)
                            }) {
                                Text("Reset All", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                            }
                        }

                        IconButton(onClick = { showFilterSheet = false }) {
                            Icon(Icons.Default.Check, contentDescription = "Apply", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Time", fontWeight = FontWeight.Bold, color = Color(0xFF1A5276))
                    Button(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ) {
                        val start = dateRangePickerState.selectedStartDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        } ?: "Start Date"
                        val end = dateRangePickerState.selectedEndDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate().toString()
                        } ?: "End Date"

                        Text("$start - $end", color = Color.Gray)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Spending", fontWeight = FontWeight.Bold, color = Color(0xFF1A5276))
                    RangeSlider(
                        value = sliderPosition,
                        onValueChange = { sliderPosition = it },
                        valueRange = 0f..5000f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.White.copy(alpha = 0.4f)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Min: R${sliderPosition.start.toInt()}", fontSize = 12.sp, color = Color(0xFF1A5276))
                        Text("Max: R${sliderPosition.endInclusive.toInt()}", fontSize = 12.sp, color = Color(0xFF1A5276))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Categories", fontWeight = FontWeight.Bold, color = Color(0xFF1A5276))
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        maxItemsInEachRow = 2,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedFilterCategories.contains(category.id),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) selectedFilterCategories.add(category.id)
                                        else selectedFilterCategories.remove(category.id)
                                    }
                                )
                                Text(category.name, color = Color(0xFF1A5276))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}