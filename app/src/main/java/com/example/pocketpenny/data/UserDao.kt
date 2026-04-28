package com.example.pocketpenny.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User): Long

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?


    @Query("SELECT EXISTS(SELECT 1 FROM user_table WHERE email = :email)")
    suspend fun checkIfEmailExists(email: String): Boolean

    @Query("SELECT * FROM user_table WHERE email = :email AND password = :password")
    suspend fun login(email: String, password: String): User?

}