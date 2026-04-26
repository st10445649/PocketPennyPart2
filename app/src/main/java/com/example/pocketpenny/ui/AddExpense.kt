@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.pocketpenny.ui

import androidx.compose.foundation.layout.*
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun AddExpenseScreen(
    dao: ExpenseDao,
    categories: List<Category>
) {
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(-1) }
    var selectedCategoryName by remember { mutableStateOf("Select Category") }
    var amount by remember { mutableFloatStateOf(0f) }
    var showCategorySheet by remember { mutableStateOf(false) }

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
                // TODO: Save to Room here later
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
            }
        }
    }
}