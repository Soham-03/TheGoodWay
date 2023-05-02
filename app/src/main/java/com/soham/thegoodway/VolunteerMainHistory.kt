package com.soham.thegoodway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.adapter.DonorHistoryAdapter
import com.soham.thegoodway.databinding.ActivityVolunteerMainHistoryBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class VolunteerMainHistory : AppCompatActivity() {
    private lateinit var binding:ActivityVolunteerMainHistoryBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerMainHistoryBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val list = ArrayList<Drive>()
        var docs: List<DocumentSnapshot>
        GlobalScope.launch {
            db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("volunteerings")
                .get()
                .addOnSuccessListener {
                    docs = it.documents
                    if(docs.isNotEmpty()){
                        for(doc in docs){
                            db.collection("drives").document(doc.id).get()
                                .addOnSuccessListener {mydoc->
                                    val loc = mydoc["location"].toString().split(", ")
                                    list.add(
                                        Drive(
                                            mydoc.id,
                                            mydoc["name"].toString(),
                                            mydoc["date"].toString(),
                                            LatLng(loc[0].toDouble(),loc[1].toDouble())
                                        )
                                    )
                                    println("List"+list)
                                    binding.recyclerViewVolunteerHistory.layoutManager = LinearLayoutManager(this@VolunteerMainHistory,
                                        RecyclerView.VERTICAL,false)
                                    binding.recyclerViewVolunteerHistory.adapter = DonorHistoryAdapter(list,this@VolunteerMainHistory)
                                }
                        }
                    }
                    else{
                        Toast.makeText(this@VolunteerMainHistory, "No donations made yet", Toast.LENGTH_SHORT).show()
                    }
                }.await()
        }
        setContentView(binding.root)
    }
}