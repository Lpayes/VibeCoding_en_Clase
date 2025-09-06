package com.example.a5biospass

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TouristAdapter(private val tourists: List<String>) : RecyclerView.Adapter<TouristAdapter.TouristViewHolder>() {
    class TouristViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvInfo: TextView = view.findViewById(android.R.id.text1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TouristViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return TouristViewHolder(view)
    }

    override fun onBindViewHolder(holder: TouristViewHolder, position: Int) {
        val nationality = tourists[position]
        holder.tvInfo.text = "Nacionalidad: $nationality"
    }

    override fun getItemCount(): Int = tourists.size
}
