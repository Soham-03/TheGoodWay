package com.soham.thegoodway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.adapter.RecyclerViewAdapter
import com.soham.thegoodway.adapter.VolunteerDrivesAdapter
import com.soham.thegoodway.databinding.ActivityDisplayDrivesBinding
import com.soham.thegoodway.databinding.ActivityDisplayDrivesVolunteerBinding

class DisplayDrivesVolunteer : AppCompatActivity() {
    private lateinit var binding:ActivityDisplayDrivesVolunteerBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: VolunteerDrivesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayDrivesVolunteerBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val date = intent.getStringExtra("date")
        val list = ArrayList<Drive>()
        db.collection("drives").get()
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val docs = it.result.documents
                    for(doc in docs){
                        val driveDate = doc["date"].toString().split("/")
                        val myDate = date!!.split("/")
                        println("Date:$myDate")
                        if(driveDate[1].toInt()>=myDate[1].toInt() && driveDate[0].toInt() >= myDate[0].toInt()){
                            val latLng = doc["location"].toString().split(", ")
                            list.add(
                                Drive(
                                    doc.id,
                                    name = doc["name"].toString(),
                                    date = doc["date"].toString(),
                                    latLng = LatLng(latLng[0].toDouble(),latLng[1].toDouble())
                                )
                            )
                        }
                    }
                }
                adapter = VolunteerDrivesAdapter(list,this)
                binding.recyclerViewDrives.layoutManager = LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL,false)
                binding.recyclerViewDrives.adapter = adapter
            }

        setContentView(binding.root)
    }
}