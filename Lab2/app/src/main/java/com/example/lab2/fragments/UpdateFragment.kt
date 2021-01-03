package com.example.lab2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lab2.R
import com.example.lab2.data.Sequence
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentUpdateBinding
import petrov.kristiyan.colorpicker.ColorPicker

class UpdateFragment : Fragment() {

    private var _binding: FragmentUpdateBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel
    private var choosedColor: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUpdateBinding.inflate(inflater, container, false)
        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        setDataToEdit(arguments?.getParcelable<Sequence>("currItem")!!)
        binding.buttonUpdate.setOnClickListener {
            updateDataToDB(arguments?.getParcelable<Sequence>("currItem")!!)
        }
        binding.btnColor.setOnClickListener{
            chooseColor()
        }
        return binding.root
    }

    private fun setDataToEdit(sequence: Sequence) {
        choosedColor = Integer.parseInt(sequence.Color)
        binding.editName.setText(sequence.Name)
        binding.btnColor.setBackgroundColor(Integer.parseInt(sequence.Color))
        binding.editCycles.setText(sequence.Cycles.toString())
        binding.editCoolDown.setText(sequence.CoolDown.toString())
        binding.editWarmUp.setText(sequence.WarmUp.toString())
        binding.editRest.setText(sequence.Rest.toString())
        binding.editWorkout.setText(sequence.Workout.toString())
        binding.editSets.setText(sequence.Sets.toString())
    }

    private fun updateDataToDB(sequence: Sequence) {
        val name = binding.editName.text.toString()
        val color = choosedColor.toString()
        val warmUp = Integer.parseInt(binding.editWarmUp.text.toString())
        val workout = Integer.parseInt(binding.editWorkout.text.toString())
        val rest = Integer.parseInt(binding.editRest.text.toString())
        val coolDown = Integer.parseInt(binding.editCoolDown.text.toString())
        val cycles = Integer.parseInt(binding.editCycles.text.toString())
        val sets = Integer.parseInt(binding.editSets.text.toString())

        val updSeq =
            Sequence(sequence.id, name, color, warmUp, workout, rest, coolDown, cycles, sets)
        mSequenceViewModel.updateSequence(updSeq)
        findNavController().navigate(R.id.action_updateFragment_to_listFragment)
    }

    private fun chooseColor() {
        val colorPicker = ColorPicker(activity)
        val colors = ArrayList<String>()
        colors.add("#EC407A")
        colors.add("#673AB7")
        colors.add("#03A9F4")
        colors.add("#FFB300")
        colors.add("#2E7D32")
        colorPicker.setColors(colors)

        colorPicker.setOnChooseColorListener(object : ColorPicker.OnChooseColorListener {
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
