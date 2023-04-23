package com.soham.thegoodway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.Orientation
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.adapter.RecyclerViewAdapter
import com.soham.thegoodway.databinding.ActivityDisplayDrivesBinding

class DisplayDrives : AppCompatActivity() {
    private lateinit var binding: ActivityDisplayDrivesBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: RecyclerViewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDisplayDrivesBinding.inflate(layoutInflater)
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
                            list.add(
                                Drive(
                                name = doc["name"].toString(),
                                date = doc["date"].toString()
                                )
                            )
                        }
                    }
                }
                adapter = RecyclerViewAdapter(list,this)
                binding.recyclerViewDrives.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
                binding.recyclerViewDrives.adapter = adapter
            }

        setContentView(binding.root)
    }

}