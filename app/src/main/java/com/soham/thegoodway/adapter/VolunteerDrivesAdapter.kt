package com.soham.thegoodway.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.soham.thegoodway.*

class VolunteerDrivesAdapter(private var list: ArrayList<Drive>, private var context: Context):
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>()  {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val btnDonate: Button = itemView.findViewById(R.id.btnDonate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerViewAdapter.ViewHolder {
        return (RecyclerViewAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.volunteer_drive_single_row, parent, false)
        ))
    }

    override fun onBindViewHolder(holder: RecyclerViewAdapter.ViewHolder, position: Int) {
        val data = list[position]
        holder.txtTitle.text = data.name
        holder.btnDonate.setOnClickListener {
            val intent = Intent(context, VolunteerSelectLocation::class.java)
            GlobalVariables.currentDrive = list[position]
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}