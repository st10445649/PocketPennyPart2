package com.example.pocketpenny.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {

    @Query("SELECT * FROM user_table WHERE username = :username AND password = :password")
    suspend fun login(username: String, password: String): User?

    @Insert
    suspend fun registerUser(user: User)

    @Insert
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM category_table")
    fun getAllCategories(): Flow<List<Category>>

    @Insert
    suspend fun insertExpense(expense: Expense)

    @Query("SELECT * FROM expense_table ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<Expense>>
}