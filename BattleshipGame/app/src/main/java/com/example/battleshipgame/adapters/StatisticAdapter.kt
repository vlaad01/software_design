package com.example.battleshipgame.adapters

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.battleshipgame.R
import com.example.battleshipgame.models.UserStatistic

class StatisticAdapter(
    private val stat: List<Pair<String, UserStatistic>>,
    private val applicationContext: Context
) : RecyclerView.Adapter<StatisticAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textGameStatus: TextView = view.findViewById<View>(R.id.textGameResult) as TextView
        val textDate: TextView = view.findViewById<View>(R.id.textDate) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.stat_item, parent, false)
        return ViewHolder(itemView)
    }

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (stat[position].second.status) {
            holder.textGameStatus.text = "WIN"
            holder.textGameStatus.setTextColor(applicationContext.resources?.getColor(R.color.green)!!)
        } else {
            holder.textGameStatus.text = "LOSE"
            holder.textGameStatus.setTextColor(applicationContext.resources?.getColor(R.color.red)!!)
        }
        holder.textDate.text = stat[position].first
    }

    override fun getItemCount(): Int {
        return stat.size
    }
}