package com.example.battleshipgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.battleshipgame.databinding.ActivityConnectToRoomBinding
import com.example.battleshipgame.models.RoomViewModel
import com.example.battleshipgame.models.RoomViewModelFactory

class ConnectToRoomActivity : AppCompatActivity() {


    private lateinit var binding: ActivityConnectToRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConnectToRoomBinding.inflate(layoutInflater)
        val roomModel =
            ViewModelProvider(this, RoomViewModelFactory())[RoomViewModel::class.java]

        binding.btnConnect.setOnClickListener {
            val roomID = binding.textRoomId.text.toString()
            roomModel.connectToRoom(roomID.toInt())
            startActivity(Intent(this@ConnectToRoomActivity, GameActivity::class.java))
            finish()
        }
        setContentView(binding.root)
    }
}