package com.example.battleshipgame.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Exclude
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class RoomViewModel : ViewModel() {

    data class Player(
        var last_step: Int = -1,
        var field: MutableList<Int> = MutableList(100) { 0 },
        var status: String = "Not ready",
        var isDefeat: Boolean = false,
    )

    var roomId: Int = 1
    var USER_TAG: String? = null
    var OPPONENT_TAG: String? = null

    var host: Player? = Player()
    var client: Player? = Player()
    var step_owner: String? = "host"

    var isStart = MutableLiveData<Boolean>()
    var isUserDefeat = MutableLiveData<Boolean>()

    var user_status: String = "Not ready"
    var opponent_status: String = "Not ready"

    private val db = Firebase.database.reference.child("rooms")


    @Exclude
    private fun toMap(): Map<String, Any?> {
        return mapOf(
            "host" to host,
            "client" to client,
            "step_owner" to step_owner,
        )
    }

    fun userDefeat() {
        db.child(roomId.toString()).child(USER_TAG.toString()).child("defeat")
            .setValue(true)
        isUserDefeat.value = true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateStatistics(isWinner: Boolean, userId: String) {
        val date = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE)
        val userStatistic = UserStatistic(isWinner)

        Firebase.database.reference.child(userId).child(date).setValue(userStatistic)
    }

    fun updateStepOwner(owner: String) {
        db.child(this.roomId.toString()).child("step_owner").setValue(owner)
    }

    fun updateUserStatus(status: String) {
        db.child(roomId.toString()).child(USER_TAG.toString()).child("status").setValue(status)
        user_status = status
        changeGameStatus()
    }

    fun createRoom() {
        this.USER_TAG = "host"
        this.OPPONENT_TAG = "client"
        this.roomId = (System.currentTimeMillis() / 1000).toInt()

        val postValues = this.toMap()
        val childUpdates = hashMapOf<String, Any>(
            "/rooms/${this.roomId}" to postValues
        )
        Firebase.database.reference.updateChildren(childUpdates)
    }

    fun connectToRoom(roomId: Int) {
        this.USER_TAG = "client"
        this.OPPONENT_TAG = "host"
        this.roomId = roomId
    }

    fun changeGameStatus() {
        if (user_status == "Ready" && opponent_status == "Ready") {
            isStart.value = true
        }
    }
}

