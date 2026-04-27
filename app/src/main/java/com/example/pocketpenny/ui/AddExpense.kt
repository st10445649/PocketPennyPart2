@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pocketpenny.ui

import android.R.attr.label
import android.os.Build
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Transaction
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.Expense
import com.example.pocketpenny.data.ExpenseDao
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddExpenseScreen(
    dao: ExpenseDao,
    categories: List<Category>
) {
    //state variables
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(-1) }
    var selectedCategoryName by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableFloatStateOf(0f) }
    var title by remember { mutableStateOf("") } // Add this line
    var showCategorySheet by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Green) }


    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    //date formatting for the UI
    val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val dateDisplayString = datePickerState.selectedDateMillis?.let {
        java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
            .format(dateFormatter)
    } ?: "Select Date"

    val scope = rememberCoroutineScope()


    //background colour
    val backgroundGradient = Brush.sweepGradient(
        colors = listOf(Color(0xff00a9fc), Color(0xff54c7ff), Color(0xffddf4ff))
    )

    //UI elements
    Box(modifier = Modifier.fillMaxSize().background(backgroundGradient)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            //heading bar that is consistent across all screens
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowLeft, contentDescription = null,
                    tint = Color.White, modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Add Transaction",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )


                Spacer(modifier = Modifier.height(24.dp))

                //card for adding expense (top card)

                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Amount", modifier = Modifier.align(Alignment.CenterHorizontally),
                            color = Color(0xffddf4ff)
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        TransactionInput(
                            value = if (amount == 0f) "" else amount.toString(),
                            onValueChange = { newValue ->
                                if (newValue.isEmpty()) {
                                    amount = 0f
                                } else {
                                    newValue.toFloatOrNull()?.let {
                                        amount = it
                                    }
                                }
                            },
                            label = "Amount"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFF0F9FF))
                                .clickable { showCategorySheet = true }
                                .padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = selectedCategoryName,
                                    modifier = Modifier.weight(1f),
                                    color = Color(0xFF5C7A89)
                                )
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = null,
                                    tint = Color(0xFF1A5276)
                                )

//                            IconButton(onClick = { showCategorySheet = true }) {
//                                Icon(Icons.Default.Add, contentDescription = "Add Category")
//                            }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        //date input and date picker
                        TransactionInput(
                            value = dateDisplayString,
                            onValueChange = {},
                            label = "Date",
                            leadingIcon = Icons.Default.DateRange,

                            modifier = Modifier.clickable { showDatePicker = true },
                            enabled = false //typing disabled so that the date picker is only used
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Section Card (Title, Description, Attachment)
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        TransactionInput(
                            value = title,
                            onValueChange = { title = it },
                            label = "Title"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        TransactionInput(
                            value = description,
                            onValueChange = { description = it },
                            label = "Description",
                            singleLine = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                TransactionInput(
                    value = "",
                    onValueChange = {},
                    label = "Add Attachment",
                    leadingIcon = Icons.Default.Share
                )

            }

            Button(
                onClick = {
                    if (amount > 0 && selectedCategoryId != -1) {
                        scope.launch {
                            dao.insertExpense(
                                Expense(
                                    amount = amount.toDouble(),
                                    description = description,
                                    categoryId = selectedCategoryId,
                                    title = title,
                                    date = datePickerState.selectedDateMillis
                                        ?: System.currentTimeMillis()
                                )
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7))
            ) {
                Text("Add Transaction", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(onClick = {
                        showDatePicker = false
                    }) { Text("Ok") }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        if (showCategorySheet) {
            ModalBottomSheet(
                onDismissRequest = { showCategorySheet = false }
            ) {

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {

                    Text(
                        "Select Category",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Existing categories
                    categories.forEach { category ->
                        Button(
                            onClick = {
                                selectedCategoryId = category.id
                                selectedCategoryName = category.name
                                showCategorySheet = false
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF64B5F6)
                            )
                        ) {
                            Text(category.name)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text("Create New Category", style = MaterialTheme.typography.titleMedium)

                    Spacer(modifier = Modifier.height(8.dp))
                    val colorOptions = listOf(
                        Color.Green,
                        Color(0xFF9575CD),
                        Color(0xff8f3a6d),
                        Color(0xFFFFF176)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        colorOptions.forEach { color ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(color, CircleShape)
                                    .clickable { selectedColor = color }
                            ) {

                                if (selectedColor == color) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .background(
                                                Color.White.copy(alpha = 0.5f),
                                                CircleShape
                                            )
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {

                        OutlinedTextField(
                            value = newCategoryName,
                            onValueChange = { newCategoryName = it },
                            label = { Text("Type in Category Name") },
                            modifier = Modifier.weight(1f)
                        )

                        IconButton(onClick = {
                            if (newCategoryName.isNotBlank()) {
                                scope.launch {
                                    val colorInt = selectedColor.toArgb()
                                    dao.insertCategory(
                                        Category(
                                            name = newCategoryName,
                                            color = colorInt
                                        )
                                    )
                                    newCategoryName = ""
                                    showCategorySheet = false
                                }
                            }
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Create Category")
                        }
                    }
                }

            }
        }
    }
}

    @Composable
    fun TransactionInput(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        leadingIcon: ImageVector? = null,
        trailingIcon: @Composable (() -> Unit)? = null,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        singleLine: Boolean = true
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(label, color = Color(0xFF5C7A89)) },
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F9FF),
                unfocusedContainerColor = Color(0xFFF0F9FF),
                disabledContainerColor = Color(0xFFF0F9FF),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            singleLine = singleLine
        )
    }
