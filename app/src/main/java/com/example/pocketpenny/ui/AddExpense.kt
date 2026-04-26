@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pocketpenny.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.ExpenseDao
import kotlinx.coroutines.launch

@Composable
fun AddExpenseScreen(
    dao: ExpenseDao,
    categories: List<Category>
)
{
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(-1) }
    var selectedCategoryName by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableFloatStateOf(0f) }
    var showCategorySheet by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(Color.Green) }

    val scope = rememberCoroutineScope()
    Column(modifier = Modifier.padding(16.dp)) {

        Text("Expense Amount: R${amount.toInt()}")

        Slider(
            value = amount,
            onValueChange = { amount = it },
            valueRange = 0f..5000f,
            steps = 50
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = selectedCategoryName,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = { showCategorySheet = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // TODO: Save expense to Room
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense")
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
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(selectedColor, CircleShape)
                            .clickable {
                                selectedColor =
                                    if (selectedColor == Color.Green) Color.Magenta else Color.Green
                            }
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = { newCategoryName = it },
                        label = { Text("Type in Category Name") },
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(onClick = {
                        if (newCategoryName.isNotBlank()) {
                            scope.launch {
                                // TODO: dao.insertCategory(...)
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