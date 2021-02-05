package com.example.battleshipgame.Fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Binder
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.battleshipgame.R
import com.example.battleshipgame.databinding.FragmentGameBinding
import com.example.battleshipgame.models.RoomViewModel
import com.example.battleshipgame.models.RoomViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class GameFragment : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var binding: FragmentGameBinding
    private val roomModel by lazy {
        ViewModelProvider(requireActivity(), RoomViewModelFactory())[RoomViewModel::class.java]
    }

    private lateinit var uid: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGameBinding.inflate(layoutInflater)
        database = Firebase.database.reference.child("rooms").child(roomModel.roomId.toString())
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("step_owner").addValueEventListener(
            object : ValueEventListener {

                @SuppressLint("ResourceAsColor")
                override fun onDataChange(snapshot: DataSnapshot) {
                    val text: String
                    if (snapshot.value.toString() == roomModel.USER_TAG) {
                        text = "Your turn"
                        binding.textTurnOwner.setTextColor(requireContext().resources.getColor(R.color.green))
                    } else {
                        text = "Opponent turn"
                        binding.textTurnOwner.setTextColor(requireContext().resources.getColor(R.color.red))
                    }
                    binding.textTurnOwner.text = text
                }

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )

        database.child(roomModel.OPPONENT_TAG.toString()).child("defeat").addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.value.toString().toBoolean()) {
                        alertDialog("You win!", true)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            }
        )

        roomModel.isUserDefeat.observe(requireActivity(), {
            if (it!!) {
                alertDialog("You lose!", false)
            }
        })

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun alertDialog(message: String, status: Boolean) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(message)
        builder.setCancelable(false)
        builder.setPositiveButton("OK") { _, _ ->
            roomModel.updateStatistics(status, uid)
            activity?.finish()
        }
        builder.create().show()
    }
}