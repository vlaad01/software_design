package com.example.lab1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.inflate
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.lab1.databinding.FragmentKeyboardBinding

class KeyboardFragment : BaseKeyboardFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_keyboard, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonSwap.setOnClickListener { swap() }
    }

    private fun swap() {
        viewModel.converter.inputId = viewModel.converter.outputId
            .also { viewModel.converter.outputId = viewModel.converter.inputId}
        viewModel.inputText.value = viewModel.outputText.value
            .also { viewModel.outputText.value = viewModel.inputText.value }
        viewModel.dataSwapped.postValue(true)
    }
}