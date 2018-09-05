package my.com.toru.sassess.ui

import android.location.Location
import android.os.Bundle
import android.os.Handler
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
import my.com.toru.sassess.R
import my.com.toru.sassess.model.DropOffLocations
import my.com.toru.sassess.util.Util
import my.com.toru.sassess.util.generateMarker
import java.util.*

class BookingActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener{
    companion object {
        private val TAG = BookingActivity::class.java.simpleName
    }

    private lateinit var googleMap:GoogleMap
    private var selectedLat:Double = 0.0
    private var selectedLng:Double = 0.0

    private var startTS:Long = 0
    private var endTS:Long = 0

    @SuppressWarnings("Unchecked")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.booking_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initSelectedPoint(){
        with(intent){
            selectedLat = getDoubleExtra(Util.SELECTED_LAT, 1.0)
            selectedLng = getDoubleExtra(Util.SELECTED_LNG, 1.0)
            startTS     = getLongExtra(Util.START_TS, 0)
            endTS       = getLongExtra(Util.END_TS, 0)

            Log.w(TAG, "selected location:: $selectedLat, $selectedLng")
            Log.w(TAG, "selected location:: $startTS, $endTS")
        }

        with(googleMap){
            addMarker(MarkerOptions()
                    .position(LatLng(selectedLat, selectedLng))
                    .title("Selected Location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(selectedLat, selectedLng), 12f))
        }
    }

    private fun initDropOffPoint(){
        Toast.makeText(this@BookingActivity, "Fetching Drop-Off Points.", Toast.LENGTH_SHORT).show()

        val dropOff = intent.getSerializableExtra("DROP_OFF") as ArrayList<DropOffLocations>
        Log.w(TAG, "==========================")
        Log.w(TAG, "drop off size:: ${dropOff.size}")

        val kmStr = getString(R.string.km_away)
        for(each in dropOff){
            Log.w(TAG, "drop off latitude:: ${each.location[0]} // drop-off longitude:: ${each.location[1]}")

            val array = FloatArray(2)
            Location.distanceBetween(selectedLat, selectedLng, each.location[0], each.location[1], array)

            for(distance in array){
                Log.w(TAG, "distance:: $distance")
            }

            val eachMarker = googleMap.addMarker(
                    MarkerOptions()
                            .generateMarker(each.location[0], each.location[1], BitmapDescriptorFactory.HUE_BLUE)
                            .title(kmStr.distance(array[0])))
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
            val bundle = Bundle()
            bundle.putLong("START_TS", startTS)
            bundle.putLong("END_TS", endTS)

            val bookinginfoDialog = BookingInfoDialogFragment.newInstance(bundle)
            bookinginfoDialog.show(supportFragmentManager, "booking_info")
        }
    }

    private fun String.distance(distance:Float):String = String.format(this, Math.round(distance / 1000f))
}