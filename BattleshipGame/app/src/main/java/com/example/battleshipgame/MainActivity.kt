package com.example.battleshipgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.battleshipgame.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()

        binding.btnLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        binding.btnProfileSettings.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileSettingsActivity::class.java))
        }

        binding.btnCreateGame.setOnClickListener {
            startActivity(Intent(this@MainActivity, CreateRoomActivity::class.java))
        }

        binding.btnConnectToGame.setOnClickListener {
            startActivity(Intent(this@MainActivity, ConnectToRoomActivity::class.java))
        }

        binding.btnUserStat.setOnClickListener {
            startActivity(Intent(this@MainActivity, UserStatisticActivity::class.java))
        }

        updateUI()
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    private fun updateUI() {
        binding.textUID.text = mAuth.currentUser!!.displayName.toString()
        binding.textEmail.text = mAuth.currentUser!!.email.toString()

        Picasso.get()
            .load(mAuth.currentUser?.photoUrl)
            .centerCrop()
            .resize(120, 120)
            .into(binding.imgUserAvatar)
    }
}