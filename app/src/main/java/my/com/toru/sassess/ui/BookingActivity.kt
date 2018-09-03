package my.com.toru.sassess.ui

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_booking.*
import my.com.toru.sassess.R
import my.com.toru.sassess.model.DropOffLocations
import java.util.*

class BookingActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{
    companion object {
        private val TAG = BookingActivity::class.java.simpleName
    }

    private lateinit var googleMap:GoogleMap
    private var selectedLat:Double = 0.0
    private var selectedLng:Double = 0.0

    @SuppressWarnings("Unchecked")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.booking_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initSelectedPoint(){
        selectedLat = intent.getDoubleExtra("SELECTED_LAT", 1.0)
        selectedLng = intent.getDoubleExtra("SELECTED_LNG", 1.0)

        Log.w(TAG, "selected location:: $selectedLat, $selectedLng")
        googleMap.addMarker(MarkerOptions()
                .position(LatLng(selectedLat, selectedLng))
                .title("Selected Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(selectedLat, selectedLng), 13f))
    }

    private fun initDropOffPoint(){
        val dropOff = intent.getSerializableExtra("DROP_OFF") as ArrayList<DropOffLocations>
        Log.w(TAG, "==========================")
        Log.w(TAG, "drop off size:: ${dropOff.size}")
        for(each in dropOff){
            Log.w(TAG, "drop off latitude:: ${each.location[0]} // drop-off longitude:: ${each.location[1]}")

            val array = FloatArray(2)
            Location.distanceBetween(selectedLat, selectedLng, each.location[0], each.location[1], array)

            for(distance in array){
                Log.w(TAG, "distance:: $distance")
            }

            googleMap.addMarker(MarkerOptions()
                    .position(LatLng(each.location[0], each.location[1]))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Distance: ${array[0]} meters away." ))
        }
        Log.w(TAG, "==========================")
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        with(googleMap){
            uiSettings.isMapToolbarEnabled = false
            setOnInfoWindowClickListener(this@BookingActivity)
        }


        initSelectedPoint()
        Handler().postDelayed({
            initDropOffPoint()
            initSelectedPoint()
        },2000)
    }

    override fun onInfoWindowClick(marker: Marker) {
        if(marker.title != "Selected Location"){
            val snackbar = Snackbar.make(booking_container, "Are you sure", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("OK"){
                // TODO: Shows detail information about reservation
                Toast.makeText(this@BookingActivity, "Completed!!", Toast.LENGTH_SHORT).show()
                snackbar.dismiss()
            }
            snackbar.show()
        }
    }
}