package com.example.pocketpenny.data

import androidx.room.*

/*
Author: Binay Shaw
Date Accessed: 27 April 2026
Link: https://proandroiddev.com/storing-data-in-local-database-like-a-boss-introducing-room-in-compose-multiplatform-2e39781c7b6a
Reason: Using RoomDb in Jetpack Compose
*/
/*
Author: Mehedi Hassan Piash
Date Accessed: 27 April 2026
Link: https://piashcse.medium.com/room-database-in-jetpack-compose-a-step-by-step-guide-for-android-development-6c7ae419105a
Reason: Using RoomDb in Jetpack Compose
*/
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