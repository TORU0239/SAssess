package my.com.toru.sassess.ui

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_main.*
import my.com.toru.sassess.R
import my.com.toru.sassess.model.BookingAvailability
import my.com.toru.sassess.remote.ApiHelper

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val TAG:String = "MainActivity"
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fab_refresh.setOnClickListener { _ ->
            mMap.clear()
            ApiHelper.getCurrentBookableCar(1535709600, 1535796000, successCB = {res ->
                Log.w(TAG, "size:: ${res.body()?.data?.size}")
                res.body()?.data?.let { list ->
                    if(list.size > 0){

                        mMap.addCircle(CircleOptions().center(LatLng(1.296793,103.786762))
                                .radius(1000.0)
                                .strokeColor(Color.RED))
                        mMap.addMarker(MarkerOptions()
                                .position(LatLng(1.296793,103.786762))
                                .title("SMOVE")
                                .snippet("DEFAULT!!"))

                        // making and adding marker on Google Map Fragment
                        for(eachItem in list){
                            val options = MarkerOptions()
                                    .position(LatLng(eachItem.location[0].toDouble(), eachItem.location[1].toDouble()))
                                    .title("Available cars: " + eachItem.availableCar)
                            val marker = mMap.addMarker(options)
                            marker.tag = eachItem
                        }

                        mMap.setOnMarkerClickListener { marker ->
                            if(!marker.title.equals("SMOVE")){
                                val tag = marker?.tag as BookingAvailability
                                tag.dropOffLocations
                                        .takeIf { it.size > 0 }
                                        .let {
                                            list->list?.let {
                                                val droppingPointLocation = StringBuilder()
                                                for(each in list){
                                                    droppingPointLocation.append("lat: ")
                                                            .append(each.location[0])
                                                            .append(", lng: ")
                                                            .append(each.location[1])
                                                            .append("\n")
                                                }
                                                info_text.text = ""
                                                info_text.text = droppingPointLocation.toString()
                                            }
                                        }
                            }

                            false
                        }
                    }
                }

            }, failedCB = {
                Log.w(TAG, "WTF!!!")
            })
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        // Add a marker in Sydney and move the camera
        mMap = googleMap
        with(mMap){
            addMarker(MarkerOptions()
                    .position(LatLng(1.296793,103.786762))
                    .title("SMOVE")
                    .snippet("DEFAULT!!"))
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(1.296793,103.786762), 14f))
        }
    }
}