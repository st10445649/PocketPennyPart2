package com.example.pocketpenny.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.room.Dao
import com.example.pocketpenny.data.Category
import com.example.pocketpenny.data.ExpenseDao
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


@Composable
fun AddExpenseScreen(dao: ExpenseDao, categories: List<Category>) {
    var description by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableIntStateOf(0) }
    var amount by remember { mutableFloatStateOf(0f) }

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
            label = {Text("Description") }
        )


        Button(onClick = {

        }) {
            Text("Save Expense")
        }
    }
}