package com.example.lab1

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.lab1.databinding.FragmentDataBinding

class DataFragment : Fragment() {
    private var binding: FragmentDataBinding? = null
    private lateinit var converter: Converter
    private val viewModel: DataViewModel by activityViewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDataBinding.bind(view)
        converter = viewModel.converter
        viewModel.inputText.observe(viewLifecycleOwner, Observer<String> {
            binding!!.textInput.text = it.toString()
        })
        viewModel.outputText.observe(viewLifecycleOwner, Observer<String> {
            binding!!.textOutput.text = it.toString()
        })
        viewModel.dataSwapped.observe(viewLifecycleOwner, Observer<Boolean> {
            binding!!.spInput.setSelection(converter.inputId)
            binding!!.spOutput.setSelection(converter.outputId)
        })

        binding!!.buttonCopyInput.setOnClickListener {
            copyToClipboard(binding!!.textInput.text.toString())}
        binding!!.buttonCopyOutput.setOnClickListener{
            copyToClipboard(binding!!.textOutput.text.toString())
        }
        binding!!.spCategory.adapter = getAdapter(converter.categories)
        binding!!.spInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                converter.inputId = 0
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                converter.inputId = position
            }
        }
        binding!!.spOutput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                converter.outputId = 0
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                converter.outputId = position
            }
        }
        binding!!.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                converter.categoryId = position
                binding!!.spInput.adapter = getAdapter(converter.getUnitList())
                binding!!.spOutput.adapter = getAdapter(converter.getUnitList())
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                converter.categoryId = 0
                binding!!.spInput.adapter = getAdapter(converter.getUnitList())
                binding!!.spOutput.adapter = getAdapter(converter.getUnitList())
            }
        }
    }

    private fun getAdapter(list: List<String>) : ArrayAdapter<String> {
        val activity = activity
        return ArrayAdapter<String>(
            activity as Context,
            R.layout.support_simple_spinner_dropdown_item,
            list
        )
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager = requireActivity()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Result", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
