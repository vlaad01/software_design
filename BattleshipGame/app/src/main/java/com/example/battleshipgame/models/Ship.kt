package com.example.battleshipgame.models

import androidx.lifecycle.ViewModel
import java.text.FieldPosition

class Ship(
    val start_x: Int,
    val start_y: Int,
    val length: Int,
    val orientation: Orientation
) {
    private var health = length

    fun isHit(position: Int): Boolean {
        val x = position / 10
        val y = position % 10
        if (orientation == Orientation.VERTICAL) {
            if (x in start_x until start_x + length && y == start_y) {
                health -= 1
                return true
            }
        } else {
            if (y in start_y until start_y + length && x == start_x) {
                health -= 1
                return true
            }
        }
        return false
    }

    fun isKilled(): Boolean {
        if (health > 0) {
            return false
        }
        return true
    }
}

enum class Orientation {
    HORIZONTAL,
    VERTICAL
}
