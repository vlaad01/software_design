package com.example.battleshipgame.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

@Suppress("UNCHECKED_CAST")
class RoomViewModelFactory : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RoomViewModel::class.java)) {
            val key = "RoomViewModel"
            if (!hashMapViewModel.containsKey(key)) {
                addViewModel(key, RoomViewModel())
            }
            return getViewModel(key) as T
        }

        throw IllegalArgumentException("ViewModel class not found")
    }

    companion object {
        val hashMapViewModel = HashMap<String, ViewModel>()

        fun addViewModel(key: String, viewModel: ViewModel) {
            hashMapViewModel.put(key, viewModel)
        }

        fun getViewModel(key: String): ViewModel? {
            return hashMapViewModel[key]
        }
    }

}