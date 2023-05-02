package com.soham.thegoodway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnSuccessListener {
                binding.txtName.text = it["name"].toString()
            }
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("donations").count().get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    binding.txtDonations.text = snapshot.count.toString()
                }
            }
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("volunteerings").count().get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    binding.txtVolunteerings.text = snapshot.count.toString()
                }
            }
        db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
            .collection("pending").count().get(AggregateSource.SERVER)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val snapshot = task.result
                    binding.txtPending.text = snapshot.count.toString()
                }
            }
        setContentView(binding.root)
    }
}
