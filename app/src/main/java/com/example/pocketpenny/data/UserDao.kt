package com.example.pocketpenny.data

import androidx.room.*

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User)

    @Query("SELECT * FROM user_table WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT EXISTS(SELECT 1 FROM user_table WHERE username = :username)")
    suspend fun checkIfUsernameExists(username: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM user_table WHERE email = :email)")
    suspend fun checkIfEmailExists(email: String): Boolean
}