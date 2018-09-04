package my.com.toru.sassess.ui

import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
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
import my.com.toru.sassess.model.GeocodeInformation
import my.com.toru.sassess.remote.ApiHelper
import my.com.toru.sassess.remote.BookingApi
import my.com.toru.sassess.remote.Util
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(selectedLat, selectedLng), 12f))
    }

    private fun initDropOffPoint(){
        Toast.makeText(this@BookingActivity, "Fetching Drop-Off Points.", Toast.LENGTH_SHORT).show()

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

            val eachMarker = googleMap.addMarker(MarkerOptions()
                    .position(LatLng(each.location[0], each.location[1]))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    .title("Distance: ${Math.round(array[0] / 1000f)} km away." ))
            eachMarker.tag = each.location
        }
        Log.w(TAG, "==========================")
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        with(googleMap){
            uiSettings.isMapToolbarEnabled = false
            setMinZoomPreference(10f)
            setOnInfoWindowClickListener(this@BookingActivity)
        }

        initSelectedPoint()
        Handler().postDelayed({
            initDropOffPoint()
            initSelectedPoint()
        },1000)
    }

    override fun onInfoWindowClick(marker: Marker) {
        if(marker.title != "Selected Location"){
            val tag = marker.tag as ArrayList<Double>

            if(Util.checkNetworkState(application)){
                getAddress(tag[0], tag[1], {
                    fl_progress_container.visibility = View.GONE
                    AlertDialog.Builder(this@BookingActivity)
                            .setTitle("Notice")
                            .setMessage("Are you sure to book $it")
                            .setNegativeButton("CANCEL"){ dialog, _ -> dialog.dismiss() }
                            .setPositiveButton("OK"){ _ , _ -> Toast.makeText(this@BookingActivity, "Completed!!", Toast.LENGTH_SHORT).show()}
                            .create()
                            .show()
                }){
                    fl_progress_container.visibility = View.GONE
                }
            }
            else{
                AlertDialog.Builder(this@BookingActivity)
                        .setTitle("Notice")
                        .setMessage("Are you sure to book here?")
                        .setNegativeButton("CANCEL"){ dialog, _ -> dialog.dismiss() }
                        .setPositiveButton("OK"){ _ , _ -> Toast.makeText(this@BookingActivity, "Completed!!", Toast.LENGTH_SHORT).show()}
                        .create()
                        .show()
            }
        }
    }

    private fun getAddress(lat:Double, lng:Double, success:(String)->Unit, fail:()->Unit){
        fl_progress_container.visibility = View.VISIBLE
        val latlng = StringBuilder().append(lat).append(",").append(lng)

        val queryMap = HashMap<String,String>()
        queryMap["latlng"] = latlng.toString()

        ApiHelper.retrofit.create(BookingApi::class.java)
                .getAddress(queryMap)
                .enqueue(object: Callback<GeocodeInformation>{
                    override fun onFailure(call: Call<GeocodeInformation>, t: Throwable) {
                        t.printStackTrace()
                        fail()
                    }

                    override fun onResponse(call: Call<GeocodeInformation>, response: Response<GeocodeInformation>) {
                        when(response.code()){
                            200->{
                                response.body()?.takeIf {
                                    it.status == "OK"
                                }?.let { geocodeInfo ->
                                    success(geocodeInfo.results[0].formattedAddress)
                                }
                            }
                            else->{
                                fail()
                            }
                        }
                    }
                })
    }
}