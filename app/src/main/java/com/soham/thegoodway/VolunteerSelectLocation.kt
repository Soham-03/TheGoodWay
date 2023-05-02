package com.soham.thegoodway

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.soham.thegoodway.databinding.ActivityDriveLocationBinding
import com.soham.thegoodway.databinding.ActivityVolunteerSelectLocationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class VolunteerSelectLocation : AppCompatActivity() {
    private lateinit var binding: ActivityVolunteerSelectLocationBinding
    private lateinit var db: FirebaseFirestore
    private lateinit var position: LatLng
    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val REQUEST_CODE = 101
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerSelectLocationBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        fetchLocation()
        with(binding.mapview) {
            // Initialise the MapView
            onCreate(null)
            // Set the map ready callback to receive the GoogleMap object
        }
        binding.btnProceedToLocation.setOnClickListener {
            db.collection("drives").document(GlobalVariables.currentDrive!!.driveId)
                .collection("donors").whereEqualTo("status","registered").get().addOnSuccessListener {
                    if(it.documents.isEmpty()){
                        val hashMap = HashMap<String,String>()
                        hashMap["location"] = currentLocation.latitude.toString()+", "+currentLocation.longitude.toString()
                        hashMap["status"] = "registered"
                        val hash = HashMap<String,String>()
                        hash["status"] = "registered"
                        db.collection("drives").document(GlobalVariables.currentDrive!!.driveId)
                            .collection("volunteers").document(FirebaseAuth.getInstance().currentUser!!.uid).set(hashMap)
                            .addOnSuccessListener {
                                Toast.makeText(this@VolunteerSelectLocation, "You are registered for volunteering of this Drive.", Toast.LENGTH_SHORT).show()
                                db.collection("users").document(FirebaseAuth.getInstance().currentUser!!.uid)
                                    .collection("pending").document(GlobalVariables.currentDrive!!.driveId)
                                    .set(hash)
                                    .addOnSuccessListener {
                                        Toast.makeText(this@VolunteerSelectLocation, "You can head back to history tab to view your pending requests", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@VolunteerSelectLocation,DonorVolunteerActivity::class.java)
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    }
                            }
                    }
                    else{
                        var myDocs : List<DocumentSnapshot> = emptyList()
                        GlobalScope.launch {
                            db.collection("drives").document(GlobalVariables.currentDrive!!.driveId)
                                .collection("donors").whereEqualTo("status","registered")
                                .get()
                                .addOnCompleteListener { docs ->
                                    myDocs = docs.result.documents
                                }
                                .await()
                            if(myDocs.isNotEmpty()){
                                val id = myDocs.random().id
                                db.collection("drives").document(GlobalVariables.currentDrive!!.driveId)
                                    .collection("donors").document(id)
                                    .get()
                                    .addOnSuccessListener {it->
                                        val location = it["location"].toString().split(", ")
                                        val lat = location[0]
                                        val long = location[1]
                                        val intent = Intent(this@VolunteerSelectLocation,DonorLocationActivity::class.java)
                                        intent.putExtra("latitude",lat)
                                        intent.putExtra("longitude",long)
                                        intent.putExtra("donorId",id)
                                        startActivity(intent)
                                    }
                            }
                        }
                    }
                }
        }
        setContentView(binding.root)
    }

    private fun fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_CODE
            )
            return
        }
        val task: Task<Location> = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener(OnSuccessListener<Location> { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    applicationContext,
                    currentLocation.latitude.toString() + "" + currentLocation.longitude,
                    Toast.LENGTH_SHORT
                ).show()
                binding.mapview.getMapAsync{
                    MapsInitializer.initialize(applicationContext)
                    setMapLocation(it,currentLocation)
                    getAddress(currentLocation.latitude,currentLocation.longitude)
                }
            }
        })
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

    fun getAddress(lat: Double, lng: Double) {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(lat, lng, 1)
            val obj: Address = addresses!![0]
            var add: String = obj.getAddressLine(0)
            Log.v("IGA", "Address$add")
//            Toast.makeText(this, add, Toast.LENGTH_SHORT).show()
            binding.txtAddress.text = add.trim()
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setMapLocation(map : GoogleMap,location: Location) {
        with(map) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(location.latitude,location.longitude), 13f))
            addMarker(MarkerOptions().position(LatLng(location.latitude,location.longitude))).title = "this is yor location"
            mapType = GoogleMap.MAP_TYPE_NORMAL
            setOnMapClickListener {
                Toast.makeText(this@VolunteerSelectLocation, "Clicked on map", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            }
        }
    }

}