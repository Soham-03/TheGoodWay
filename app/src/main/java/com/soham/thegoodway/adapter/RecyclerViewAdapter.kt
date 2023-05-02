package com.soham.thegoodway.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.soham.thegoodway.DonorSelectLocation
import com.soham.thegoodway.Drive
import com.soham.thegoodway.GlobalVariables
import com.soham.thegoodway.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter(private var list: ArrayList<Drive>,private var context: Context):
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val btnDonate: Button = itemView.findViewById(R.id.btnDonate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return (ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.drive_single_row,parent,false)))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = list[position]
        holder.txtTitle.text = data.name
        holder.btnDonate.setOnClickListener {
            val intent = Intent(context,DonorSelectLocation::class.java)
            GlobalVariables.currentDrive = list[position]
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(newList: ArrayList<Drive>) {
        list = newList
        notifyDataSetChanged()
    }
}