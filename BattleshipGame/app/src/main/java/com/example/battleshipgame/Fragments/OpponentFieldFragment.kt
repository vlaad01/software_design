package com.example.battleshipgame.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.ViewModelProvider
import com.example.battleshipgame.R
import com.example.battleshipgame.adapters.FieldAdapter
import com.example.battleshipgame.databinding.FragmentOpponentFieldBinding
import com.example.battleshipgame.models.RoomViewModel
import com.example.battleshipgame.models.RoomViewModelFactory
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class OpponentFieldFragment : Fragment(), AdapterView.OnItemClickListener {

    private lateinit var binding: FragmentOpponentFieldBinding

    private val roomModel by lazy {
        ViewModelProvider(requireActivity(), RoomViewModelFactory())[RoomViewModel::class.java]
    }
    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOpponentFieldBinding.inflate(inflater)
        binding.opponentGridField.adapter = FieldAdapter(requireContext(), MutableList(100) { 0 })
        binding.opponentGridField.onItemClickListener = this

        database = Firebase.database.reference.child("rooms")
            .child(roomModel.roomId.toString())
        Log.e("ID", roomModel.roomId.toString())

        database.child(roomModel.OPPONENT_TAG.toString()).child("field").addChildEventListener(
            object : ChildEventListener {

                @SuppressLint("UseCompatLoadingForDrawables")
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                    val index = snapshot.key!!.toInt()
                    if (index != -1) {
                        val value = snapshot.value.toString().toInt()
                        val item = binding.opponentGridField.getChildAt(index)
                        Log.e("VALUE", value.toString())
                        if (value == 2) {
                            item.background = context!!.getDrawable(R.drawable.ic_hit)
                        } else {
                            item.background = context!!.getDrawable(R.drawable.ic_clear)
                        }
                    }

                }

                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                }

                override fun onCancelled(error: DatabaseError) {
                }
            }
        )

        database.child("step_owner").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    roomModel.step_owner = snapshot.getValue().toString()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )
        return binding.root
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (roomModel.step_owner == roomModel.USER_TAG)
            database.child(roomModel.USER_TAG.toString()).child("last_step").setValue(id)
    }
}