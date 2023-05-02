package com.soham.thegoodway

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.adapter.DonorHistoryAdapter
import com.soham.thegoodway.adapter.RecyclerViewAdapter
import com.soham.thegoodway.databinding.ActivityDonorHistoryBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class DonorHistory : AppCompatActivity() {
    private lateinit var binding: ActivityDonorHistoryBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorHistoryBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val list = ArrayList<Drive>()
        var docs: List<DocumentSnapshot>
        GlobalScope.launch {
            db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid).collection("donations")
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
                                    binding.recyclerViewDonorHistory.layoutManager = LinearLayoutManager(this@DonorHistory,RecyclerView.VERTICAL,false)
                                    binding.recyclerViewDonorHistory.adapter = DonorHistoryAdapter(list,this@DonorHistory)
                                }
                        }
                    }
                    else{
                        Toast.makeText(this@DonorHistory, "No donations made yet", Toast.LENGTH_SHORT).show()
                    }
                }.await()
        }
        setContentView(binding.root)
    }
}