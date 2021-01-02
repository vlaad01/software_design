package com.example.lab2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sequence_table")
data class Sequence(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val Name: String,
    val Color: String,
    val WarmUp: Int,
    val Workout: Int,
    val Rest: Int,
    val CoolDown: Int,
    val Cycles: Int,
    val Sets: Int,
)