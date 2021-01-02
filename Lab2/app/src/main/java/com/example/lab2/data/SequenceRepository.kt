package com.example.lab2.data

import androidx.lifecycle.LiveData

class SequenceRepository(private val sequenceDao: SequenceDao) {

    val readAllData: LiveData<List<Sequence>> = sequenceDao.readAllData()

    suspend fun addSequence(sequence: Sequence){
        sequenceDao.addSequence(sequence)
    }

}