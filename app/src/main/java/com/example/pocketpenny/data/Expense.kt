package com.example.pocketpenny.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val amount: Double,
    val date: Long,
    val description: String,
    val title: String,
    val categoryId: Int,
    val filePath: String? = null
)