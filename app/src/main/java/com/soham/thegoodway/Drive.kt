package com.soham.thegoodway

import com.google.android.gms.maps.model.LatLng

data class Drive(
    val driveId:String,
    val name:String,
    val date:String,
    val latLng:LatLng
)
