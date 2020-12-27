package com.example.lab1

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataViewModel : ViewModel() {
    val inputText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val outputText: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    val dataSwapped: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>()
    }

    val converter = Converter()
}