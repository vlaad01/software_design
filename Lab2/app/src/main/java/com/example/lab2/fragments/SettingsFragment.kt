package com.example.lab2.fragments

import android.content.DialogInterface
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentSettingsBinding
import java.util.*


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!
    private val langList = arrayOf("ru", "en")
    private val langNames = arrayOf("Русский", "English" )
    private lateinit var mSequenceViewModel: SequenceViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)

        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
            binding.dayNightSwitch.isChecked = true
        binding.dayNightSwitch.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        })

        binding.buttonPlus.setOnClickListener{
            onFontChanged(1.1F)
        }
        binding.buttonMinus.setOnClickListener {
            onFontChanged(0.9F)
        }
        binding.buttonLang.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setItems(langNames) { _, which ->
                updateLocale(Locale(langList[which]))
            }
            val dialog = builder.create()
            dialog.show()
        }
        binding.buttonDeleteAll.setOnClickListener {
            mSequenceViewModel.deleteAll()
        }


        return binding.root
    }


    fun onFontChanged(scale: Float) {
        val configuration: Configuration = resources.configuration
        configuration.fontScale *= scale
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        activity?.recreate()
    }

    fun updateLocale(locale: Locale) {
        val configuration = resources.configuration
        configuration.setLocale(locale)
        getResources().updateConfiguration(configuration, getResources().getDisplayMetrics())
        activity?.recreate()
    }
}