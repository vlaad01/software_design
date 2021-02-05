package com.example.battleshipgame.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.battleshipgame.R
import com.example.battleshipgame.adapters.FieldAdapter
import com.example.battleshipgame.databinding.FragmentWaitRoomBinding
import com.example.battleshipgame.models.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class WaitRoomFragment : Fragment(), AdapterView.OnItemClickListener {

    private val fieldModel by lazy { ViewModelProvider(requireActivity())[FieldViewModel::class.java] }
    private val roomModel by lazy {
        ViewModelProvider(requireActivity(), RoomViewModelFactory())[RoomViewModel::class.java]
    }

    private lateinit var binding: FragmentWaitRoomBinding
    private lateinit var database: DatabaseReference
    private var choosedShip: ShipType = ShipType.BOAT
    private var choosedOrientation: Orientation = Orientation.HORIZONTAL
    private var userStatus = false

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWaitRoomBinding.inflate(inflater)
        binding.userGridField.adapter = FieldAdapter(requireContext(), MutableList(100) { 0 })
        binding.userGridField.onItemClickListener = this

        binding.shipTypeGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioBoat.id -> choosedShip = ShipType.BOAT
                binding.radioCruiser.id -> choosedShip = ShipType.CRUISER
                binding.radioDestroyer.id -> choosedShip = ShipType.DESTROYER
                binding.radioBattleship.id -> choosedShip = ShipType.BATTLESHIP
            }
        }

        binding.positionGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                binding.radioHorizontal.id -> choosedOrientation = Orientation.HORIZONTAL
                binding.radioVertical.id -> choosedOrientation = Orientation.VERTICAL
            }
        }

        fieldModel.lastChangedCell.observe(requireActivity(), Observer {
            val item = binding.userGridField.getChildAt(it.first)
            if (item != null)
                item.background = requireContext().getDrawable(R.drawable.ic_ship)
        })

        binding.btnReady.setOnClickListener {
            findNavController().navigate(R.id.to_game_fragment)
        }

        Firebase.database.reference.child("rooms").child(roomModel.roomId.toString())
            .child(roomModel.OPPONENT_TAG.toString()).child("status").addValueEventListener(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        roomModel.opponent_status = snapshot.getValue(String::class.java).toString()
                        roomModel.changeGameStatus()
                    }
                    override fun onCancelled(error: DatabaseError) {
                    }
                }
            )

        roomModel.isStart.observe(requireActivity(), {
            if (it!!)
                if (findNavController().currentDestination?.id == R.id.waitRoomFragment) {
                    fieldModel.USER_TAG = roomModel.USER_TAG.toString()
                    fieldModel.OPPONENT_TAG = roomModel.OPPONENT_TAG.toString()
                    fieldModel.roomId = roomModel.roomId.toString()
                    findNavController().navigate(R.id.to_game_fragment)
                }
        })

        binding.btnReady.setOnClickListener {
            if (!userStatus) {
                userStatus = true
                binding.btnReady.text = "I'm not ready"
                roomModel.updateUserStatus("Ready")
                roomModel.changeGameStatus()
            } else {
                userStatus = false
                binding.btnReady.text = "I'm ready"
                roomModel.updateUserStatus("Not ready")
            }
        }



        return binding.root
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val ship = Ship(
            position / 10, position % 10,
            choosedShip.size, choosedOrientation
        )
        Log.e("COUNT", choosedShip.count.toString())
        if (choosedShip.count > 0) {
            if (!fieldModel.addShip(ship)) {
                Toast.makeText(
                    requireContext(),
                    "Choose another place!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                choosedShip.count -= 1
            }
        }
    }
}

enum class ShipType(val size: Int, var count: Int) {
    BOAT(1, 4),
    CRUISER(2, 3),
    DESTROYER(3, 2),
    BATTLESHIP(4, 1);
}

