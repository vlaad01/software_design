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
import com.example.lab2.databinding.FragmentAddBinding

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater, container, false)
        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        binding.buttonAdd.setOnClickListener {
            insertDataToDB()
        }
        return binding.root
    }

    private fun insertDataToDB() {
        val name = binding.editName.text.toString()
        val color = binding.editColor.text.toString()
        val warmUp = Integer.parseInt(binding.editWarmUp.text.toString())
        val workout = Integer.parseInt(binding.editWorkout.text.toString())
        val rest = Integer.parseInt(binding.editRest.text.toString())
        val coolDown = Integer.parseInt(binding.editCoolDown.text.toString())
        val cycles = Integer.parseInt(binding.editCycles.text.toString())
        val sets = Integer.parseInt(binding.editSets.text.toString())

        val sequence = Sequence(0, name, color, warmUp, workout, rest, coolDown, cycles, sets)
        mSequenceViewModel.addSequence(sequence)
        Toast.makeText(requireContext(), "Added!", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_addFragment_to_listFragment)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}