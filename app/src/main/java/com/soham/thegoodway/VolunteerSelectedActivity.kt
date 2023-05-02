package com.soham.thegoodway

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.databinding.ActivityVolunterSelectedBinding

class VolunteerSelectedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunterSelectedBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunterSelectedBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val hash = HashMap<String,String>()
        hash["location"] = GlobalVariables.donorLocation!!.latitude.toString()+", "+GlobalVariables.donorLocation!!.longitude.toString()
        hash["status"] = "registered"
        binding.txtVolunteerName.text = GlobalVariables.selectedVolunteer
        binding.btnConfirmVolunteer.setOnClickListener {
            println("Volunteer: "+GlobalVariables.selectedVolunteer)
            db.collection("users").document(GlobalVariables.selectedVolunteer!!)
                .collection("pending").document(GlobalVariables.currentDrive!!.driveId).update("status","requested","donor",
                    FirebaseAuth.getInstance().currentUser!!.uid)
                .addOnSuccessListener {
                    db.collection("drives").document(GlobalVariables.currentDrive!!.driveId)
                        .collection("donors").document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .set(hash)
                        .addOnSuccessListener {
                            Toast.makeText(this@VolunteerSelectedActivity, "Volunteer will contact you soon", Toast.LENGTH_SHORT).show()
                        }
                }
        }
        setContentView(binding.root)
    }
}