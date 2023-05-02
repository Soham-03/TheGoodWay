package com.soham.thegoodway.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.LatLng
import com.soham.thegoodway.*

class VolunteerHistoryAdapter(private var list: ArrayList<HistoryVolunteer>, private var context: Context):
    RecyclerView.Adapter<VolunteerHistoryAdapter.ViewHolder>()  {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val txtTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val btnDonate: Button = itemView.findViewById(R.id.btnDonate)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return (VolunteerHistoryAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.volunteer_drive_single_row, parent, false)
        ))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: VolunteerHistoryAdapter.ViewHolder, position: Int) {
        val data = list[position]
        val db = FirebaseFirestore.getInstance()
        var location: List<String> = emptyList()
        db.collection("drives").document(data.driveId).get().addOnSuccessListener {
            holder.txtTitle.text = it["name"].toString()
            db.collection("drives").document(data.driveId).collection("donors")
                .document(data.donor).get().addOnSuccessListener {th->
                    location = th["location"].toString().split(", ")
                }
        }
        if(data.driveStatus == "requested"){
            holder.btnDonate.text = data.driveStatus
        }
        else{
            holder.btnDonate.text = "Pending"
        }
        holder.btnDonate.setOnClickListener {
            if(data.driveStatus == "requested"){
//                val hash = HashMap<String,String>()
//                hash["name"] = data.driveName
//                db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
//                    .collection("pending").document(data.driveId).update("status","confirmed")
//                    .addOnSuccessListener {
//                        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
//                            .collection("volunteerings").document(data.driveId).update("status","confirmed")
//                        db.collection("users").document(data.donor).collection("donations")
//                            .document(data.driveId).set(hash)
//                            .addOnSuccessListener {
                                val intent = Intent(context,VolunteeringConfirmationActivity::class.java)
                                intent.putExtra("donor",data.donor)
                                intent.putExtra("driveId",data.driveId)
                                val loc = location
                                intent.putExtra("latitude",loc[0])
                                intent.putExtra("longitude",loc[1])
                                context.startActivity(intent)
//                            }
//                    }
            }
            else{
                Toast.makeText(context, "No Volunteering Requests Available\nCheck back later", Toast.LENGTH_SHORT).show()
            }
        }
    }
}