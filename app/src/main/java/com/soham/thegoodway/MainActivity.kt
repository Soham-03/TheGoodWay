package com.soham.thegoodway

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.soham.thegoodway.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        auth = FirebaseAuth.getInstance()
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this@MainActivity,RegisterLoginActivity::class.java)
            intent.putExtra("task","Register")
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this@MainActivity,RegisterLoginActivity::class.java)
            intent.putExtra("task","LogIn")
            startActivity(intent)
        }

        setContentView(binding.root)
    }

    override fun onStart() {
        if(auth.currentUser!=null){
            val intent = Intent(this@MainActivity,DonorVolunteerActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        super.onStart()
    }

}
