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

class DataFragment : BaseDataFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_data, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.buttonCopyInput.setOnClickListener{
            copyToClipboard(binding!!.textInput.text.toString())
        }

        binding!!.buttonCopyOutput.setOnClickListener{
            copyToClipboard(binding!!.textOutput.text.toString())
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboardManager = requireActivity()
            .getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Result", text)
        clipboardManager.setPrimaryClip(clipData)
    }
}
