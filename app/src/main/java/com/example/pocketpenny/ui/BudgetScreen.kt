package com.example.pocketpenny.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pocketpenny.data.Category

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(categories: List<Category>) {

    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var minAmount by remember { mutableStateOf("") }
    var maxAmount by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "Create Budget",
            style = MaterialTheme.typography.headlineMedium
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Category") },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                            message = ""
                        }
                    )
                }
            }
        }

        OutlinedTextField(
            value = minAmount,
            onValueChange = {
                minAmount = it
                message = ""
            },
            label = { Text("Minimum Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = maxAmount,
            onValueChange = {
                maxAmount = it
                message = ""
            },
            label = { Text("Maximum Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                val min = minAmount.toDoubleOrNull()
                val max = maxAmount.toDoubleOrNull()

                message = when {
                    selectedCategory == null -> "Please select a category"
                    minAmount.isBlank() -> "Please enter a minimum amount"
                    maxAmount.isBlank() -> "Please enter a maximum amount"
                    min == null -> "Minimum amount must be a valid number"
                    max == null -> "Maximum amount must be a valid number"
                    min < 0 -> "Minimum amount cannot be negative"
                    max < 0 -> "Maximum amount cannot be negative"
                    max <= min -> "Maximum amount must be greater than minimum amount"
                    else -> "Budget details are valid"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Budget")
        }

        if (message.isNotBlank()) {
            Text(message)
        }
    }
}