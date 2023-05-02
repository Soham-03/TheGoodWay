package com.soham.thegoodway

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.databinding.ActivityVolunteeringConfirmationBinding

class VolunteeringConfirmationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteeringConfirmationBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteeringConfirmationBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val donor = intent.getStringExtra("donor")
        val driveId = intent.getStringExtra("driveId")
        val lat = intent.getStringExtra("latitude")
        val lng = intent.getStringExtra("longitude")
        db.collection("users").document(donor!!).get().addOnSuccessListener {
            binding.txtDonorName.text = it["name"].toString()
            binding.txtDonorNumber.text = it["phone"].toString()
        }
        val hash = HashMap<String,String>()
        hash["name"] = " "
        binding.btnConfirmVolunteer.setOnClickListener {
            hash["name"] = ""
            db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("pending").document(driveId!!).update("status","confirmed")
                .addOnSuccessListener {
                    db.collection("drives").document(driveId).collection("donors").document(donor!!).update("status","confirmed")
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .collection("volunteerings").document(driveId).set(hash)
                    db.collection("users").document(donor).collection("donations")
                        .document(driveId).set(hash)
                        .addOnSuccessListener {
                            val navigationIntentUri: Uri =
                                Uri.parse("google.navigation:q=" +lat!!.toDouble()+ "," + lng!!.toDouble()) //creating intent with latlng
                            val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                            Toast.makeText(this@VolunteeringConfirmationActivity, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        }
                }
        }
        setContentView(binding.root)
    }
}