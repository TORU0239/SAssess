package my.com.toru.sassess.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import my.com.toru.sassess.R
import my.com.toru.sassess.model.DropOffLocations
import java.util.*

class BookingActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private val TAG = BookingActivity::class.java.simpleName
    }

    @SuppressWarnings("Unchecked")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking)

        val dropOff = intent.getSerializableExtra("DROP_OFF") as ArrayList<DropOffLocations>
        val selectedLat = intent.getDoubleExtra("SELECTED_LAT", 1.0)
        val selectedLng = intent.getDoubleExtra("SELECTED_LNG", 1.0)

        Log.w(TAG, "selected location:: $selectedLat, $selectedLng")
        Log.w(TAG, "==========================")
        Log.w(TAG, "drop off size:: ${dropOff.size}")
        for(each in dropOff){
            Log.w(TAG, "drop off latitude:: ${each.location[0]} // drop-off longitude:: ${each.location[1]}")
        }
        Log.w(TAG, "==========================")


        val mapFragment = supportFragmentManager.findFragmentById(R.id.booking_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {}
}
