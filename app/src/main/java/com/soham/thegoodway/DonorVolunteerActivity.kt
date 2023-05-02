package com.soham.thegoodway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.soham.thegoodway.databinding.ActivityDonorVolunteerBinding
import com.soham.thegoodway.databinding.ActivityMainBinding

class DonorVolunteerActivity : AppCompatActivity() {
    lateinit var binding: ActivityDonorVolunteerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorVolunteerBinding.inflate(layoutInflater)
        binding.btnDonor.setOnClickListener {
            val intent = Intent(this@DonorVolunteerActivity,DonorActivity::class.java)
            startActivity(intent)
        }
        binding.btnVolunteer.setOnClickListener {
            val intent = Intent(this@DonorVolunteerActivity,VolunteerActivity::class.java)
            startActivity(intent)
        }
        binding.btnProfile.setOnClickListener {
            val intent = Intent(this@DonorVolunteerActivity,ProfileActivity::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)
    }
}