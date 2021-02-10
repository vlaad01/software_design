package com.example.battleshipgame.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.battleshipgame.R
import com.example.battleshipgame.adapters.FieldAdapter
import com.example.battleshipgame.databinding.FragmentFieldBinding
import com.example.battleshipgame.models.*
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FieldFragment : Fragment() {
    private val fieldModel by lazy { ViewModelProvider(requireActivity())[FieldViewModel::class.java] }
    private val roomModel by lazy {
        ViewModelProvider(requireActivity(), RoomViewModelFactory())[RoomViewModel::class.java]
    }

    private lateinit var binding: FragmentFieldBinding
    private lateinit var database: DatabaseReference

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFieldBinding.inflate(inflater)

        database = Firebase.database.reference.child("rooms")
            .child(roomModel.roomId.toString())

        binding.userGridField.adapter = FieldAdapter(requireContext(), fieldModel.getUserField())

        fieldModel.lastChangedCell.observe(requireActivity(), Observer {
            val item = binding.userGridField.getChildAt(it.first)

            when (it.second) {
                1 -> item.background = requireContext().getDrawable(R.drawable.ic_clear)
                2 -> item.background = requireContext().getDrawable(R.drawable.ic_hit)
            }
        })

        fieldModel.isDefeat.observe(requireActivity(), {
            if (it!!) {
                roomModel.userDefeat()
            }
        })

        database.child(roomModel.OPPONENT_TAG.toString()).child("last_step")
            .addValueEventListener(
                object : ValueEventListener {

                    @SuppressLint("UseCompatLoadingForDrawables")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val index = snapshot.getValue(Long::class.java)!!.toInt()
                        if (index != -1)
                            if(!fieldModel.hit(index)) {
                                roomModel.updateStepOwner(roomModel.USER_TAG.toString())
                            }
                    }

                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            )

        return binding.root
    }


}