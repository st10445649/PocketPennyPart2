package com.example.pocketpenny.ui

import android.R.attr.fontWeight
import android.R.attr.label
import android.R.attr.onClick
import android.R.attr.padding
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.pocketpenny.R
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.Expense
import com.example.pocketpenny.data.ExpenseDao
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionScreen(navController: NavController, expenseDao: ExpenseDao) {
    val expenses by expenseDao.getAllExpenses().collectAsState(initial = emptyList())
    val categories by expenseDao.getAllCategories().collectAsState(initial = emptyList())
    val selectedFilterCategories = remember { mutableStateListOf<Int>() }
    var sliderPosition by remember { mutableStateOf(0f..1_000_000f) }
    val dateRangePickerState = rememberDateRangePickerState()

    var showDatePicker by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    val filteredExpenses = remember(
        expenses,
        selectedFilterCategories.size,
        sliderPosition,
        dateRangePickerState.selectedStartDateMillis,
        dateRangePickerState.selectedEndDateMillis
    ) {
        expenses.filter { expense ->
            val matchesCategory =
                selectedFilterCategories.isEmpty() || selectedFilterCategories.contains(expense.categoryId)
            val matchesAmount =
                expense.amount >= sliderPosition.start && expense.amount <= sliderPosition.endInclusive
            val matchesDate =
                if (dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null) {
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
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )
    val isFiltered = selectedFilterCategories.isNotEmpty() ||
            sliderPosition.start > 0f ||
            sliderPosition.endInclusive < 5000f ||
            dateRangePickerState.selectedStartDateMillis != null

    //UI elements
    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(top = 40.dp, bottom = 20.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Transactions",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 80.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showFilterSheet = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.filter_icon),
                                contentDescription = "Filter",
                                tint = Color(0xFF54C7FF),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        // The "Reset" button only appears when filters are on

                        if(isFiltered) {
                            TextButton(onClick = {
                                selectedFilterCategories.clear()
                                sliderPosition = 0f..5000f
                                dateRangePickerState.setSelection(null, null)
                            }) {
                                Text("Reset Filters", color = Color(0xFF1A5276), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            }
                        } else

                            Text("All", color = Color(0xFF1A5276), fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)

                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { navController.navigate("add_expense") }) {
                            Icon(
                                Icons.Default.Add,
                                contentDescription = "Add",
                                tint = Color(0xFF54C7FF),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE1F5FE), thickness = 1.dp)

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
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            BottomNavigationBar(navController)
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

    //filter sheet design
    if (showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = Color(0xff99ddfe),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            dragHandle = null
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                    Text(
                        "Apply Filters",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    IconButton(onClick = { showFilterSheet = false }) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = "Apply",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                //Time filter space
                Text(
                    "Time",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A5276),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FilterDateButton(
                        label = dateRangePickerState.selectedStartDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                .toString()
                        } ?: "Start Date",
                        icon = Icons.Default.CalendarMonth,
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                    FilterDateButton(
                        label = dateRangePickerState.selectedEndDateMillis?.let {
                            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                                .toString()
                        } ?: "End Date",
                        icon = Icons.Default.CalendarMonth,
                        onClick = { showDatePicker = true },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                //spending space
                Text(
                    "Spending",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A5276),
                    fontSize = 18.sp
                )
                RangeSlider(
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    valueRange = 0f..10000f,
                    modifier = Modifier.fillMaxWidth(),
                    startThumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            colors = SliderDefaults.colors(thumbColor = Color.White),
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    endThumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            colors = SliderDefaults.colors(thumbColor = Color.White),
                            modifier = Modifier.size(30.dp)
                        )
                    },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = Color.White,
                        inactiveTrackColor = Color.White.copy(alpha = 0.4f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Min. R${" %,.0f".format(sliderPosition.start)}",
                        fontSize = 12.sp,
                        color = Color(0xFF1A5276)
                    )
                    Text(
                        "Max. R${" %,.0f".format(sliderPosition.endInclusive)}",
                        fontSize = 12.sp,
                        color = Color(0xFF1A5276)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                //category filter area
                Text(
                    "Categories",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A5276),
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    maxItemsInEachRow = 2,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = selectedFilterCategories.contains(category.id),
                                onCheckedChange = { isChecked ->
                                    if (isChecked) selectedFilterCategories.add(category.id)
                                    else selectedFilterCategories.remove(category.id)
                                },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color.White,
                                    uncheckedColor = Color.White,
                                    checkmarkColor = Color(0xFF64B5F6)
                                )
                            )
                            Text(category.name, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun FilterDateButton(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color(0xFF64B5F6), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, color = Color.Gray, fontSize = 14.sp, maxLines = 1)
        }
    }
}
@Composable
fun TransactionItem(expense: Expense, dao: ExpenseDao) {

    var category by remember { mutableStateOf<Category?>(null) }
    //state to check if image dialog is opened
    var showImageDialog by remember { mutableStateOf(false) }

    LaunchedEffect(expense.categoryId) {
        category = dao.getCategoryById(expense.categoryId)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
            .clickable(enabled = expense.filePath != null) {
                showImageDialog = true
            },
        verticalAlignment = Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(Color(category?.color ?: Color.Gray.toArgb()), CircleShape)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(expense.title, fontWeight = FontWeight.Bold, color = Color(0xFF1A5276))

            //show description if there
            if (!expense.description.isNullOrBlank()) {
                Text(
                    text = expense.description,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            // Only show image/link if there is an image attached
            if (expense.filePath != null) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.attach),
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = Color(0xFF54C7FF)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("View Receipt", fontSize = 12.sp, color = Color(0xFF54C7FF))
            }
        }

    }
        Text(
            "R ${String.format("%.2f",expense.amount)} ",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A5276)
        )
    }
        //Popup to show
            if (showImageDialog && expense.filePath != null) {
                AlertDialog(
                    onDismissRequest = { showImageDialog = false },
                    confirmButton = {
                        TextButton(onClick = { showImageDialog = false }) {
                            Text("Close", color = Color(0xFF1A5276))
                        }
                    },
                    title = { Text(text = "Receipt: ${expense.title}") },
                    text = {

                        AsyncImage(
                            model = expense.filePath,
                            contentDescription = "Attachment",
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Fit
                        )
                    }
                )
            }
}
