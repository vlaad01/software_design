package com.example.lab2.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SequenceViewModel(application: Application): AndroidViewModel(application) {

    val readAllData: LiveData<List<Sequence>>
    private val repository: SequenceRepository

    init {
        val sequenceDao = SequenceDatabase.getDatabase(application).sequenceDao()
        repository = SequenceRepository(sequenceDao)
        readAllData = repository.readAllData
    }

    fun addSequence(sequence: Sequence){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addSequence(sequence)
        }
    }

    fun deleteSequence(sequence: Sequence) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSequence(sequence)
        }
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAll()
        }
    }

}