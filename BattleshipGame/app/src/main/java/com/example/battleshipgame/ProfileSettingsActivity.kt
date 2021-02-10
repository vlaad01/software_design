package com.example.battleshipgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.battleshipgame.databinding.ActivityProfileSettingsBinding
import com.google.firebase.auth.FirebaseAuth

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileSettingsBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        mAuth = FirebaseAuth.getInstance()

        setContentView(binding.root)
    }
}