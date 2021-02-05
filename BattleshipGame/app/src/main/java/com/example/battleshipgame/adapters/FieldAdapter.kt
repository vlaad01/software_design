package com.example.battleshipgame.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageView
import com.example.battleshipgame.R

internal open class FieldAdapter(
    val context: Context,
    val fieldMatrix: MutableList<Int>
) : BaseAdapter() {

    private val width = 10
    val height = 10

    override fun getCount(): Int {
        return width * height
    }

    override fun getItem(position: Int): Any {
        return 0
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.grid_item, null)
        if (fieldMatrix[position] == 0) {
            view.background = context.getDrawable(R.drawable.ic_wave)
        } else {
            view.background = context.getDrawable(R.drawable.ic_ship)
        }
        return view
    }
}