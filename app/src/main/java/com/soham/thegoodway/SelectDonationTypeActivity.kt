package com.soham.thegoodway

import android.R
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.soham.thegoodway.databinding.ActivitySelectDonationTypeBinding


class SelectDonationTypeActivity : AppCompatActivity() {
    private val paths = arrayOf("Food", "Clothes", "Stationary", "Others")
    private var selectedType = ""
    private lateinit var binding: ActivitySelectDonationTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectDonationTypeBinding.inflate(layoutInflater)
        val adapter = ArrayAdapter(
            this@SelectDonationTypeActivity,
            R.layout.simple_spinner_item, paths
        )

        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.spinner.adapter = adapter
        binding.spinner.onItemSelectedListener = object :AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedType = paths[p2]
                GlobalVariables.selectedDonationType = selectedType
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                //
            }

        }

        binding.btnProceed.setOnClickListener {
            val intent = Intent(this@SelectDonationTypeActivity,ScheduleDrivesCalendarActivity::class.java)
            startActivity(intent)
        }

        setContentView(binding.root)
    }
}