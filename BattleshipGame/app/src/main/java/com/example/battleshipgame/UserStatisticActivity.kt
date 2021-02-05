package com.example.battleshipgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import com.example.battleshipgame.adapters.StatisticAdapter
import com.example.battleshipgame.databinding.ActivityUserStatisticBinding
import com.example.battleshipgame.models.UserStatistic
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UserStatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserStatisticBinding
    private lateinit var currentUser: FirebaseUser
    private lateinit var db: DatabaseReference
    private val statistic = mutableListOf<Pair<String, UserStatistic>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserStatisticBinding.inflate(layoutInflater)
        currentUser = FirebaseAuth.getInstance().currentUser!!
        db = Firebase.database.reference.child("user_statistics")

        db.child(currentUser.uid).addValueEventListener(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (item in snapshot.children) {
                        val date = item.key!!
                        val result = item.child("status").value.toString()
                        val statItem = UserStatistic(result.toBoolean())
                        statistic.add(Pair(date, statItem))
                    }
                    updateUI()

                }

                override fun onCancelled(error: DatabaseError) {
                }

            }
        )

        setContentView(binding.root)
    }

    fun updateUI() {
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        val adapter = StatisticAdapter(statistic, applicationContext)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        binding.listView.adapter = adapter
        binding.listView.layoutManager = linearLayoutManager
        binding.listView.adapter?.notifyDataSetChanged()
    }
}