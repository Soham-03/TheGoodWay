package com.soham.thegoodway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.adapter.VolunteerHistoryAdapter
import com.soham.thegoodway.databinding.ActivityVolunteerHistoryBinding

class VolunteerHistory : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerHistoryBinding
    private lateinit var adapter: VolunteerHistoryAdapter
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        binding = ActivityVolunteerHistoryBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val list = ArrayList<HistoryVolunteer>()
        db.collection("users").document(uid).collection("pending").where(Filter.or(Filter.equalTo("status","registered"),Filter.equalTo("status","requested")))
            .addSnapshotListener { it, error ->
                val docs = it!!.documents
                list.clear()
                for(doc in docs){
                    list.add(
                        HistoryVolunteer(
                            doc.id,
                            doc["status"].toString(),
                            doc["donor"].toString()
                    )
                    )
                }
                adapter = VolunteerHistoryAdapter(list,this@VolunteerHistory)
                binding.recyclerVolunteerHistory.layoutManager = LinearLayoutManager(this@VolunteerHistory,RecyclerView.VERTICAL,false)
                binding.recyclerVolunteerHistory.adapter = adapter
            }

        setContentView(binding.root)
    }
}