package com.example.lab2.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.example.lab2.data.Sequence
import com.example.lab2.databinding.CustomRowBinding

class ListAdapter : Adapter<ListAdapter.MyViewHolder>() {
    class MyViewHolder(var binding: CustomRowBinding ) : ViewHolder(binding.root) {
    }

    private var sequenceList = emptyList<Sequence>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = CustomRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return sequenceList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = sequenceList[position]
        holder.binding.textName.text = currentItem.Name
        holder.binding.textCycles.text = currentItem.Cycles.toString()
        holder.binding.textCoolDown.text = currentItem.CoolDown.toString()
        holder.binding.textRest.text = currentItem.Rest.toString()
        holder.binding.textWarmUp.text = currentItem.WarmUp.toString()
        holder.binding.textSets.text = currentItem.Sets.toString()
        holder.binding.textWorkOut.text = currentItem.Workout.toString()

        holder.binding.btnDelete.setOnClickListener {
            val tmpList = this.sequenceList as MutableList<Sequence>
            tmpList.removeAt(position)
            this.sequenceList = tmpList
            notifyItemRemoved(position)
            notifyItemRangeChanged(0, itemCount)
        }
    }

    fun setData(sequence: List<Sequence>) {
        this.sequenceList = sequence
        notifyDataSetChanged()
    }

}