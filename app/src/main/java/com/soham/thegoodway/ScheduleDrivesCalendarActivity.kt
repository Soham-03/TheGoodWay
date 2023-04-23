package com.soham.thegoodway

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import com.soham.thegoodway.databinding.ActivityScheduleDrivesCalendarBinding
import java.util.Calendar

class ScheduleDrivesCalendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScheduleDrivesCalendarBinding
    private lateinit var calendar: Calendar
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScheduleDrivesCalendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        calendar = Calendar.getInstance()

        binding.calendarView.setOnDateChangeListener { calendarView, i, i2, i3 ->
            val intent = Intent(this@ScheduleDrivesCalendarActivity,DisplayDrives::class.java)
            val date = "${i3}/${i2+1}/${i}"
            intent.putExtra("date",date)
            startActivity(intent)
        }
    }
}