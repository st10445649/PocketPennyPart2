package com.example.pocketpenny.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_table")
data class Budget(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val categoryId: Int?,
    val minAmount: Double,
    val maxAmount: Double,
    val monthYear: String
)