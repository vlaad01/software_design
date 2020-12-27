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

class KeyboardFragment : Fragment(R.layout.fragment_keyboard) {

    private var binding: FragmentKeyboardBinding? = null
    private val viewModel: DataViewModel by activityViewModels()
    private var floatNum: Boolean = false
    private lateinit var converter: Converter

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
        binding = FragmentKeyboardBinding.bind(view)
        converter = viewModel.converter
        viewModel.inputText.value = ""
        viewModel.outputText.value = ""
        binding!!.button0.setOnClickListener { editInput("0", false) }
        binding!!.button1.setOnClickListener { editInput("1", false) }
        binding!!.button2.setOnClickListener { editInput("2", false) }
        binding!!.button3.setOnClickListener { editInput("3", false) }
        binding!!.button4.setOnClickListener { editInput("4", false) }
        binding!!.button5.setOnClickListener { editInput("5", false) }
        binding!!.button6.setOnClickListener { editInput("6", false) }
        binding!!.button7.setOnClickListener { editInput("7", false) }
        binding!!.button8.setOnClickListener { editInput("8", false) }
        binding!!.button9.setOnClickListener { editInput("9", false) }
        binding!!.buttonDot.setOnClickListener { editInput(".", true) }
        binding!!.buttonBackspace.setOnClickListener { backSpace() }
        binding!!.buttonClear.setOnClickListener { clear() }
        binding!!.buttonSwap.setOnClickListener { swap() }
    }

    private fun editInput(string: String, dot: Boolean) {
        if (dot && floatNum) {
            return
        } else if (dot) {
            floatNum = true
        }
        viewModel.inputText.value += string
        viewModel.outputText.value = viewModel.inputText.value?.let { converter.convert(it) }
    }

    private fun backSpace() {
        var string = viewModel.inputText.value
        if (string!!.isNotEmpty()) {
            if (string[string.length - 1] == '.') {
                floatNum = false
            }
            string = string.substring(0, string.length - 1)
            viewModel.inputText.value = string
            viewModel.outputText.value = converter.convert(string)
        }
        return
    }

    private fun clear() {
        viewModel.inputText.value = ""
        viewModel.outputText.value = ""
        floatNum = false
    }

    private fun swap() {
        viewModel.converter.inputId = viewModel.converter.outputId
            .also { viewModel.converter.outputId = viewModel.converter.inputId}
        viewModel.inputText.value = viewModel.outputText.value
            .also { viewModel.outputText.value = viewModel.inputText.value }
        viewModel.dataSwapped.postValue(true)
    }
}