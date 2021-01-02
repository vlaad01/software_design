package com.example.lab2.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SequenceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSequence(sequence: Sequence)

    @Delete
    suspend fun deleteSequence(sequence: Sequence)

    @Query("DELETE FROM sequence_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM sequence_table ORDER BY id ASC")
    fun readAllData(): LiveData<List<Sequence>>

}