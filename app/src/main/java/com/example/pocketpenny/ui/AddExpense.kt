package com.example.pocketpenny.ui

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp

@Composable
fun AddExpenseScreen(dao: BudgetDao, categories: List<Category>) {
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(0) }
    var amount by remember { mutableFloatStateOf(0f) } // Using a SeekBar for logic practice

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Expense Amount: R${amount.toInt()}")
        Slider(
            value = amount,
            onValueChange = { amount = it },
            valueRange = 0f..5000f,
            steps = 50
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") }
        )

        // Category Selection would go here (DropdownMenu)

        Button(onClick = {
            /* Logic to call dao.insertExpense(...) */
        }) {
            Text("Save Expense")
        }
    }
}