package com.example.lab2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab2.R
import com.example.lab2.data.Sequence
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentAddBinding
import petrov.kristiyan.colorpicker.ColorPicker
import petrov.kristiyan.colorpicker.ColorPicker.OnChooseColorListener


class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel
    private var choosedColor: Int = -1294214

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        binding.buttonAdd.setOnClickListener {
            insertDataToDB()
        }
        binding.btnColor.setBackgroundColor(choosedColor)
        binding.btnColor.setOnClickListener{
            chooseColor()
        }
        return binding.root
    }

    private fun insertDataToDB() {
        val name = binding.editName.text.toString()
        val color = choosedColor.toString()
        val warmUp = Integer.parseInt(binding.editWarmUp.text.toString())
        val workout = Integer.parseInt(binding.editWorkout.text.toString())
        val rest = Integer.parseInt(binding.editRest.text.toString())
        val coolDown = Integer.parseInt(binding.editCoolDown.text.toString())
        val cycles = Integer.parseInt(binding.editCycles.text.toString())
        val sets = Integer.parseInt(binding.editSets.text.toString())

        val sequence = Sequence(0, name, color, warmUp, workout, rest, coolDown, cycles, sets)
        mSequenceViewModel.addSequence(sequence)
        findNavController().navigate(R.id.action_addFragment_to_listFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun chooseColor() {
        val colorPicker = ColorPicker(activity)
        val colors = ArrayList<String>()
        colors.add("#EC407A")
        colors.add("#673AB7")
        colors.add("#03A9F4")
        colors.add("#FFB300")
        colors.add("#2E7D32")
        colorPicker.setColors(colors)

        colorPicker.setOnChooseColorListener(object : OnChooseColorListener {
            override fun onChooseColor(position: Int, color: Int) {
                binding.btnColor.setBackgroundColor(color)
                choosedColor = color
            }

            override fun onCancel() {
            }
        })
        colorPicker.show()
    }
}