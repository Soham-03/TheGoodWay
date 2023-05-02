package com.soham.thegoodway

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.databinding.ActivityDonorLocationBinding
import java.io.IOException
import java.util.*
import kotlin.math.ln

class DonorLocationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDonorLocationBinding
    private lateinit var db: FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDonorLocationBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        val lat = intent.getStringExtra("latitude")
        val lng = intent.getStringExtra("longitude")
        val donor = intent.getStringExtra("donorId")
        val hash = HashMap<String,String>()
        hash["name"] = " "
        with(binding.mapview) {
            onCreate(null)
        }
        getAddress(lat!!.toDouble(),lng!!.toDouble())
        binding.btnPickup.setOnClickListener {
            hash["name"] = " "
            db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("pending").document(GlobalVariables.currentDrive!!.driveId).update("status","confirmed")
                .addOnSuccessListener {
                    db.collection("drives").document(GlobalVariables.currentDrive!!.driveId).collection("donors").document(donor!!).update("status","confirmed")
                    db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .collection("volunteerings").document(GlobalVariables.currentDrive!!.driveId).set(hash)
                    db.collection("users").document(donor).collection("donations")
                        .document(GlobalVariables.currentDrive!!.driveId).set(hash)
                        .addOnSuccessListener {
                            val navigationIntentUri: Uri =
                                Uri.parse("google.navigation:q=" +lat.toDouble()+ "," + lng.toDouble()) //creating intent with latlng
                            val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                            Toast.makeText(this@DonorLocationActivity, "Saved Successfully", Toast.LENGTH_SHORT).show()
                        }
                }
        }
        setContentView(binding.root)
    }
    private fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
            val obj: Address = addresses!![0]
            var add: String = obj.getAddressLine(0)
            Log.v("IGA", "Address$add")
            binding.txtAddress.text = add.trim()
            binding.mapview.getMapAsync{
                MapsInitializer.initialize(applicationContext)
                setMapLocation(it, LatLng(lat,lng))
            }

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMapLocation(map : GoogleMap, location: LatLng) {
        println("Lat"+location.latitude+"Long:"+location.longitude)
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 13f))
            addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude))).title = "this is yor location"
            mapType = GoogleMap.MAP_TYPE_NORMAL
        }
    }

    override fun onResume() {
        binding.mapview.onResume()
        super.onResume()
    }


    override fun onPause() {
        super.onPause()
        binding.mapview.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapview.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapview.onLowMemory()
    }
}