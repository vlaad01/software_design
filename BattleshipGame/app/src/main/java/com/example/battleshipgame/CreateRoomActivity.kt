package com.example.battleshipgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.example.battleshipgame.databinding.ActivityCreateRoomBinding
import com.example.battleshipgame.models.RoomViewModel
import com.example.battleshipgame.models.RoomViewModelFactory

class CreateRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateRoomBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomModel =
            ViewModelProvider(this, RoomViewModelFactory())[RoomViewModel::class.java]
        roomModel.createRoom()
        binding = ActivityCreateRoomBinding.inflate(layoutInflater)
        binding.textRoomId.setText(roomModel.roomId.toString())

        binding.btnStartGame.setOnClickListener {
            startActivity(Intent(this@CreateRoomActivity, CreateGameActivity::class.java))
            finish()
        }

        setContentView(binding.root)
    }
}