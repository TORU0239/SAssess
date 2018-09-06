package my.com.toru.sassess.util

import android.Manifest
import android.app.Activity
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import my.com.toru.sassess.R

fun Snackbar.actionAndRequestPermission(act: Activity){
    this.setAction(R.string.ok){
        ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Util.REQUEST_CODE)
    }.show()
}

fun MarkerOptions.generateMarker(lat:Double,
                                 lng:Double):MarkerOptions = this.generateMarker(lat, lng, BitmapDescriptorFactory.HUE_RED)

fun MarkerOptions.generateMarker(lat:Double,
                                 lng:Double,
                                 color:Float):MarkerOptions =
        this.position(LatLng(lat,lng)).icon(BitmapDescriptorFactory.defaultMarker(color))

fun String.distance(distance:Float):String = String.format(this, Math.round(distance / 1000f))