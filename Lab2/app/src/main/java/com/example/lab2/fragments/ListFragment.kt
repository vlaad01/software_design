package com.example.lab2.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab2.R
import com.example.lab2.data.Sequence
import com.example.lab2.data.SequenceViewModel
import com.example.lab2.databinding.FragmentListBinding

class ListFragment : Fragment() {

    private var _binding: FragmentListBinding? = null
    private val binding get() = _binding!!
    private lateinit var mSequenceViewModel: SequenceViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentListBinding.inflate(inflater, container, false)
        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }
        val adapter = ListAdapter(object : OnClickListener {
            override fun itemDelete(sequence: Sequence) {
                mSequenceViewModel.deleteSequence(sequence)
            }
            override fun itemEdit(sequence: Sequence) {
                findNavController().navigate(R.id.action_listFragment_to_updateFragment,
                    bundleOf("currItem" to sequence))
            }

            override fun timer(sequence: Sequence) {
                findNavController().navigate(R.id.action_listFragment_to_timerFragment,
                    bundleOf("currItem" to sequence))
            }
        })
        val recyclerView = binding.recyclerView

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mSequenceViewModel = ViewModelProvider(this).get(SequenceViewModel::class.java)
        mSequenceViewModel.readAllData.observe(viewLifecycleOwner, Observer { sequence ->
            adapter.setData(sequence)
        })
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.settings_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_settings) {
            findNavController().navigate(R.id.action_listFragment_to_settingsFragment)
        }
        return super.onOptionsItemSelected(item)
    }
}