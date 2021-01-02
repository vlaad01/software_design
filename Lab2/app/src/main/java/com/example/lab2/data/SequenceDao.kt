package com.example.lab2.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SequenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSequence(sequence: Sequence)

    @Query("SELECT * FROM sequence_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Sequence>>

}