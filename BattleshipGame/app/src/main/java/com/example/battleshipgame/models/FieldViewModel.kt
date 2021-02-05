package com.example.battleshipgame.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.lang.Integer.max
import java.lang.Integer.min

class FieldViewModel : ViewModel() {
    var USER_TAG: String? = null
    var OPPONENT_TAG: String? = null
    var roomId: String = ""
    var lastChangedCell = MutableLiveData<Pair<Int, Int>>()
    var isDefeat = MutableLiveData<Boolean>()

    private val db = Firebase.database.reference.child("rooms")
    private val dbMatrix = MutableList(100) { 0 }
    private val userFieldMatrix = MutableList(100) {0}
    private val shipList = mutableListOf<Ship>()

    private fun updateData(position: Int, value: Int) {
        dbMatrix[position] = value
        db.child(roomId).child(USER_TAG!!).child("field")
            .updateChildren(mapOf(position.toString() to value))
        lastChangedCell.value = Pair(position, value)
    }

    fun hit(position: Int): Boolean {
        for (ship in shipList) {
            if (ship.isHit(position)) {
                updateData(position, 2)
                if (ship.isKilled()) {
                    val cellList = getNeighborCells(ship)
                    for (cell in cellList) {
                        if(dbMatrix[cell] == 0) {
                            updateData(cell, 1)
                        }
                    }
                    shipList.remove(ship)
                    
                    if (shipList.isEmpty()) {
                        isDefeat.value = true
                    }
                }
                return true
            }
        }
        updateData(position, 1)
        return false
    }

    private fun getNeighborCells(ship: Ship): MutableList<Int> {
        val start_x = max(ship.start_x - 1, 0)
        val start_y = max(ship.start_y - 1, 0)
        val end_x: Int
        val end_y: Int

        val cellList = mutableListOf<Int>()

        if (ship.orientation == Orientation.VERTICAL) {
            end_x = min(ship.start_x + ship.length, 9)
            end_y = min(ship.start_y + 1, 9)
        } else {
            end_x = min(ship.start_x + 1, 9)
            end_y = min(ship.start_y + ship.length, 9)
        }
        for (i in start_x..end_x) {
            for (j in start_y..end_y) {
                val index = i * 10 + j
                cellList.add(index)
            }
        }
        return cellList
    }

    fun addShip(ship: Ship): Boolean {

        if (ship.orientation == Orientation.VERTICAL) {
            if (ship.start_x + ship.length - 1 > 9)
                return false
        }
        else {
            if (ship.start_y + ship.length - 1> 9)
                return false
        }

        val cellList = getNeighborCells(ship)
        for (cell in cellList) {
            if (userFieldMatrix[cell] != 0) {
                return false
            }
        }

        if (ship.orientation == Orientation.VERTICAL) {
            for (x in ship.start_x until ship.start_x + ship.length) {
                val index = x * 10 + ship.start_y
                userFieldMatrix[index] = 1
                lastChangedCell.value = Pair(index, 3)
            }
        } else {
            for (y in ship.start_y until ship.start_y + ship.length) {
                val index = ship.start_x * 10 + y
                userFieldMatrix[index] = 1
                lastChangedCell.value = Pair(index, 3)
            }
        }
        shipList.add(ship)
        return true
    }

    fun getUserField() = userFieldMatrix
}
