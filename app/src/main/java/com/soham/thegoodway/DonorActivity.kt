package com.soham.thegoodway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.soham.thegoodway.databinding.ActivityDonorBinding

class DonorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorBinding.inflate(layoutInflater)
        binding.btnNewDonation.setOnClickListener {
            val intent = Intent(this@DonorActivity,SelectDonationTypeActivity::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)
    }
}