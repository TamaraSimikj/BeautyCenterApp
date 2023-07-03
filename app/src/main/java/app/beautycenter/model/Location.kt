package app.beautycenter.model

import com.google.android.gms.maps.model.LatLng

data class Location(
    val name: String,
    val latLng: LatLng
)