package my.com.toru.sassess.ui

import android.app.Activity
import android.content.Intent
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
import com.google.android.gms.maps.model.*
import my.com.toru.sassess.R
import my.com.toru.sassess.model.DropOffLocations
import my.com.toru.sassess.util.Util
import my.com.toru.sassess.util.distance
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
                    .title(getString(R.string.your_pickup_point))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(selectedLat, selectedLng), 12f))
        }
    }

    private fun initDropOffPoint(){
        Toast.makeText(this@BookingActivity, R.string.fetched_drop_off, Toast.LENGTH_SHORT).show()
        val dropOff = intent.getSerializableExtra(Util.DROP_OFF) as ArrayList<DropOffLocations>
        val kmStr = getString(R.string.km_away)
        for(each in dropOff){
            val array = FloatArray(2)
            Location.distanceBetween(selectedLat, selectedLng, each.location[0], each.location[1], array)
            val eachMarker = googleMap.addMarker(
                    MarkerOptions()
                            .generateMarker(each.location[0], each.location[1], BitmapDescriptorFactory.HUE_BLUE)
                            .title(kmStr.distance(array[0])))
            eachMarker.tag = each.location
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        with(googleMap){
            uiSettings.isMapToolbarEnabled = false
            setMinZoomPreference(10f)
            setOnInfoWindowClickListener(this@BookingActivity)
            setLatLngBoundsForCameraTarget(LatLngBounds(LatLng(Util.SOUTHMOST_LAT, Util.WESTMOST_LNG), LatLng(Util.NORTHMOST_LAT, Util.EASTMOST_LNG)))
        }

        initSelectedPoint()
        Handler().postDelayed({
            initDropOffPoint()
            initSelectedPoint()
        },1000)
    }

    override fun onInfoWindowClick(marker: Marker) {
        if(marker.title != getString(R.string.your_pickup_point)){
            val bundle = Bundle()
            bundle.putLong(Util.START_TS, startTS)
            bundle.putLong(Util.END_TS, endTS)

            val booking = BookingInfoDialogFragment
                    .newInstance(bundle){
                        startActivityForResult(Intent(this@BookingActivity, BookingCompleteActivity::class.java), 0x10)
                    }
            booking.show(supportFragmentManager, Util.BOOKING_DIALOG_TAG)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 0x10 && resultCode == Activity.RESULT_OK){
            setResult(Activity.RESULT_OK)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}