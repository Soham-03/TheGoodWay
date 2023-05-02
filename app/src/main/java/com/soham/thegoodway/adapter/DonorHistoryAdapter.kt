package com.soham.thegoodway.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soham.thegoodway.DonorSelectLocation
import com.soham.thegoodway.Drive
import com.soham.thegoodway.GlobalVariables
import com.soham.thegoodway.R

class DonorHistoryAdapter (private var list: ArrayList<Drive>, private var context: Context):
    RecyclerView.Adapter<DonorHistoryAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (DonorHistoryAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.history_single_row, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.txtTitle.text = data.name
    }

    override fun getItemCount(): Int {
        return list.size
    }
}