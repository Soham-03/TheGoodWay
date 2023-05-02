package com.soham.thegoodway

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.databinding.ActivityDriveLocationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class DriveLocationActivity : AppCompatActivity() {
    private lateinit var currentLocation: Location
    private lateinit var binding: ActivityDriveLocationBinding
    private lateinit var db:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDriveLocationBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        with(binding.mapview) {
            onCreate(null)
        }
        getAddress(GlobalVariables.currentDrive!!.latLng.latitude,GlobalVariables.currentDrive!!.latLng.longitude)
        binding.btnNavigate.setOnClickListener {
            val hashMap = HashMap<String,String>()
            hashMap["type"] = GlobalVariables.selectedDonationType!!
            hashMap["status"] = "confirmed"
            hashMap["location"] = GlobalVariables.donorLocation!!.latitude.toString()+", "+GlobalVariables.donorLocation!!.longitude.toString()
            db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection("donations").document(GlobalVariables.currentDrive!!.driveId).set(hashMap)
                .addOnSuccessListener {
                    db.collection("drives").document(GlobalVariables.currentDrive!!.driveId)
                        .collection("donors").document(FirebaseAuth.getInstance().currentUser!!.uid)
                        .set(hashMap)
                        .addOnSuccessListener {
                            val navigationIntentUri: Uri =
                                Uri.parse("google.navigation:q=" +GlobalVariables.currentDrive!!.latLng.latitude+ "," + GlobalVariables.currentDrive!!.latLng.longitude) //creating intent with latlng
                            val mapIntent = Intent(Intent.ACTION_VIEW, navigationIntentUri)
                            mapIntent.setPackage("com.google.android.apps.maps")
                            startActivity(mapIntent)
                            Toast.makeText(this@DriveLocationActivity, "Donation added to history", Toast.LENGTH_SHORT).show()
                        }
                }
        }
        binding.btnGetVolunteer.setOnClickListener {
            val listOfVolunteer = ArrayList<String>()
            GlobalScope.launch {
                db.collection("drives").document(GlobalVariables.currentDrive!!.driveId).collection("volunteers").whereEqualTo("status","registered").get()
                    .addOnSuccessListener {docss->
                        val docs1 = docss.documents
                        for(mydoc in docs1){
                            println("Volunteers:"+mydoc.id)
                            listOfVolunteer.add(mydoc.id)
                        }
                    }
                    .await()
                if(listOfVolunteer.isNotEmpty()){
                    GlobalVariables.selectedVolunteer = listOfVolunteer.random()
                    val intent = Intent(this@DriveLocationActivity,VolunteerSelectedActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Looper.prepare()
                    Toast.makeText(this@DriveLocationActivity, "No Volunteers available right now, check after some time", Toast.LENGTH_SHORT).show()
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
                setMapLocation(it,LatLng(lat,lng))
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this@DriveLocationActivity,DonorVolunteerActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

}