package com.example.pocketpenny.data

import androidx.room.Entity
import androidx.room.PrimaryKey
/*
Author: Android Developers
Date Accessed: 27 April 2026
Link: https://developer.android.com/training/data-storage/room/defining-data
Reason: Guidelines for creating entities to save to RoomDB
*/

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String
)