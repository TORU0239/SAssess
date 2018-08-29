package my.com.toru.sassess.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import my.com.toru.sassess.R
import my.com.toru.sassess.remote.ApiHelper

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG:String = "MainActivity"
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fab_refresh.setOnClickListener {
            ApiHelper.getCurrentBookableCar(1535709600, 1535796000, successCB = {res ->
                Log.w(TAG, "size:: ${res.body()?.data?.size}")
            }, failedCB = {
                Log.w(TAG, "WTF!!!")
            })
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
}