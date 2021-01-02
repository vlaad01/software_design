package com.example.lab2.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab2.R
import com.example.lab2.data.Sequence
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentListBinding

class ListFragment : Fragment(){

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.floatingActionButton.setOnClickListener{
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
        val adapter = ListAdapter()
        val recyclerView = binding.recyclerView

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        mSequenceViewModel.readAllData.observe(viewLifecycleOwner, Observer {
            sequence -> adapter.setData(sequence)
        })

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun deleteSequence(sequence: Sequence) {
        mSequenceViewModel.deleteSequence(sequence)
    }

}