package com.soham.thegoodway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.soham.thegoodway.databinding.ActivityVolunteerBinding

class VolunteerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerBinding.inflate(layoutInflater)
        binding.btnNewContribution.setOnClickListener {
            val intent = Intent(this@VolunteerActivity,ScheduleDrivesCalendarActivity::class.java)
            intent.putExtra("comingFrom","volunteer")
            startActivity(intent)
        }
        binding.btnHistory.setOnClickListener {
            val intent = Intent(this@VolunteerActivity,VolunteerMainHistory::class.java)
            startActivity(intent)
        }
        binding.txtPending.setOnClickListener {
            val intent = Intent(this@VolunteerActivity,VolunteerHistory::class.java)
            startActivity(intent)
        }
        setContentView(binding.root)
    }
}