package com.example.pocketpenny.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_table")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: Long,
    val startTime: String,
    val endTime: String,
    val description: String,
    val categoryId: Int,
    val filePath: String? = null
)