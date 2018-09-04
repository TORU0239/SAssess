package my.com.toru.sassess.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_main.*
import my.com.toru.sassess.R
import my.com.toru.sassess.SassApp
import my.com.toru.sassess.model.BookingAvailability
import my.com.toru.sassess.remote.ApiHelper
import my.com.toru.sassess.remote.Util
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    companion object {
        private const val TAG:String = "MainActivity"
        private const val COUNT = 1

        /* INFORMATION OF TERRITORY OF SINGAPORE */
        private const val NORTHMOST_LAT = 1.470556
        private const val NORTHMOST_LNG = 103.817222

        private const val WESTMOST_LAT = 1.242538
        private const val WESTMOST_LNG = 103.6047383

        private const val SOUTHMOST_LAT = 1.238450
        private const val SOUTHMOST_LNG = 103.832928

        private const val EASTMOST_LAT = 1.349264
        private const val EASTMOST_LNG = 104.043313
    }

    private lateinit var map: GoogleMap

    private lateinit var calendar:Calendar
    private lateinit var secondCalendar:Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkPermission()

        // initialization for first calendar
        val calendarYear    = Calendar.getInstance().get(Calendar.YEAR)
        val calendarMonth   = Calendar.getInstance().get(Calendar.MONTH)
        val calendarDay     = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val calendarHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val calendarMinutes = Calendar.getInstance().get(Calendar.MINUTE)

        calendar = GregorianCalendar(calendarYear, calendarMonth, calendarDay)
        calendar.set(Calendar.HOUR_OF_DAY, calendarHour)
        calendar.set(Calendar.MINUTE, calendarMinutes)

        secondCalendar = GregorianCalendar(calendarYear, calendarMonth, calendarDay+1, calendarHour, calendarMinutes)

        first_date_txt.text = StringBuilder()
                .append(calendarYear).append("/")
                .append(calendarMonth+1).append("/")
                .append(calendarDay)


        first_time_txt.text = StringBuilder()
                                .append(calendarHour).append(":")
                                .let {
                                    if(calendarMinutes in 0..9){
                                        it.append(0).append(calendarMinutes)
                                    }
                                    else{
                                        it.append(calendarMinutes)
                                    }
                                }.toString()

        first_date_txt.setOnClickListener {
            DatePickerDialog(this@MainActivity, DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
                val firstDate = StringBuilder()
                                                .append(year).append("/")
                                                .append(month+1).append("/")
                                                .append(dayOfMonth)
                first_date_txt.text = firstDate.toString()

            }, calendarYear, calendarMonth, calendarDay).show()
        }

        first_time_txt.setOnClickListener {
            TimePickerDialog(this@MainActivity, TimePickerDialog.OnTimeSetListener {
                _, hourOfDay, minute ->
                val minutes = if(minute in 0..9){
                    """0$minute"""
                } else{
                    minute.toString()
                }

                val firstTime = StringBuilder()
                        .append(hourOfDay).append(":")
                        .append(minutes)
                first_time_txt.text = firstTime.toString()

                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)

            }, calendarHour, calendarMinutes,true).show()
        }

        second_date_txt.text = StringBuilder()
                .append(secondCalendar.get(Calendar.YEAR)).append("/")
                .append(secondCalendar.get(Calendar.MONTH)+1).append("/")
                .append(secondCalendar.get(Calendar.DAY_OF_MONTH))

        second_time_txt.text = StringBuilder()
                .append(secondCalendar.get(Calendar.HOUR_OF_DAY)).append(":")
                .let {
                    if(secondCalendar.get(Calendar.MINUTE) in 0..9){
                        it.append(0).append(calendarMinutes)
                    }
                    else{
                        it.append(calendarMinutes)
                    }
                }.toString()

        second_date_txt.setOnClickListener {
            val datepicker = DatePickerDialog(this@MainActivity, DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMonth ->
                val secondDate = StringBuilder()
                        .append(year).append("/")
                        .append(month+1).append("/")
                        .append(dayOfMonth)
                second_date_txt.text = secondDate.toString()

                secondCalendar.set(Calendar.YEAR, year)
                secondCalendar.set(Calendar.MONTH, month)
                secondCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            }, secondCalendar.get(Calendar.YEAR), secondCalendar.get(Calendar.MONTH), secondCalendar.get(Calendar.DAY_OF_MONTH))
            datepicker.show()
        }

        second_time_txt.setOnClickListener {
            TimePickerDialog(this@MainActivity, TimePickerDialog.OnTimeSetListener {
                _, hourOfDay, minute ->
                val minutes = if(minute in 0..9){
                    """0$minute"""
                } else{
                    minute.toString()
                }
                val secondTime = StringBuilder()
                        .append(hourOfDay).append(":")
                        .append(minutes)
                second_time_txt.text = secondTime.toString()

                secondCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                secondCalendar.set(Calendar.MINUTE, minute)

            }, secondCalendar.get(Calendar.HOUR_OF_DAY), secondCalendar.get(Calendar.MINUTE),true)
                    .show()
        }


        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fab_refresh.setOnClickListener { _ ->
            if(Util.checkNetworkState(this)){
                Log.i(TAG, "firstTS: ${calendar.timeInMillis/1000}, secondTS: ${secondCalendar.timeInMillis/1000}")
                progress_main.visibility = View.VISIBLE
                ApiHelper.getCurrentBookableCar(calendar.timeInMillis/1000, secondCalendar.timeInMillis/1000, successCB = {res ->
                    progress_main.visibility = View.GONE
                    Log.w(TAG, "size:: ${res.body()?.data?.size}")
                    res.body()?.data?.let { list ->
                        if(list.size > 0){
                            map.clear()
                            map.addMarker(MarkerOptions()
                                    .position(LatLng((application as SassApp).fixedCurrentLatitude, (application as SassApp).fixedCurrentLongitude))
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))

                            // making and adding marker on Google Map Fragment

                            for(eachItem in list){
                                val options = MarkerOptions()
                                        .position(LatLng(eachItem.location[0], eachItem.location[1]))
                                        .title("Available cars: " + eachItem.availableCar)
                                val marker = map.addMarker(options)
                                marker.tag = eachItem
                            }
                        }
                    }

                }, failedCB = {
                    Log.w(TAG, "WTF!!!")
                    progress_main.visibility = View.GONE
                    Snackbar.make(ll_booking_info, "Unknown Error, Please Try again.", Snackbar.LENGTH_LONG)
                            .show()
                })
            }
            else{
                Snackbar.make(ll_booking_info, "Check your internet connection", Snackbar.LENGTH_LONG)
                        .show()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.i(TAG, "onMapReady")
        map = googleMap
        with(map){
            if(checkPermission()){
                isMyLocationEnabled = true
                setOnInfoWindowClickListener(this@MainActivity)
            }
            else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(ll_booking_info, "Location Permission is needed.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK"){
                                ActivityCompat.requestPermissions(this@MainActivity,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                        0x00)
                            }
                            .show()
                }
                else{
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0x00)
                }
            }
            uiSettings.isMapToolbarEnabled = false
            setMinZoomPreference(10f)
        }
    }

    private lateinit var locationMgr:LocationManager
    private var c = 0

    private fun initLocationManager(){
        locationMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        with(locationMgr) {
            if(checkPermission()){
                requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1f, locationListener)
                requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 1f, locationListener)
            }
            else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
                    Snackbar.make(ll_booking_info, "Location Permission is needed.",
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction("OK"){
                                ActivityCompat.requestPermissions(this@MainActivity,
                                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                        0x00)
                            }
                            .show()
                }
                else{
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0x00)
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
        if(!marker?.title.equals("SMOVE")){
            val tag = marker?.tag as BookingAvailability
            tag.dropOffLocations
                    .takeIf { it.size > 0 }
                    .let {
                        list->list?.let {
                        Log.w(TAG, "list size::: ${list.size}")
                        for(each in list){
                            Log.w(TAG, "drop off lat:: ${each.location[0]}, drop off lng:: ${each.location[1]}")
                        }
                        val intent = Intent(this@MainActivity, BookingActivity::class.java)
                                .putExtra("DROP_OFF", list)
                                .putExtra("SELECTED_LAT", tag.location[0])
                                .putExtra("SELECTED_LNG", tag.location[1])
                        startActivity(intent)
                        marker.hideInfoWindow()
                    }
                }
        }
    }

    private val locationListener:LocationListener = object:LocationListener{
        override fun onLocationChanged(location: Location?) {
            Log.i(TAG, "onLocationChanged")
            if(c == COUNT){
                c = 0
                locationMgr.removeUpdates(this)
            }
            else{
                Log.i(TAG, "current provider:: ${location?.provider}")
                Log.i(TAG, "latitude:${location?.latitude}, longitude:${location?.longitude}")

                location?.let {
                    if(isUserInsideSG(it.latitude, it.longitude)){
                        Log.w(TAG, "user are in SG!!")
                        map.moveCamera(CameraUpdateFactory.newLatLng(LatLng(it.latitude, it.longitude)))
                        map.addMarker(MarkerOptions()
                                .position(LatLng(it.latitude, it.longitude))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))
                        with(application as SassApp){
                            fixedCurrentLatitude = location.latitude
                            fixedCurrentLongitude = location.longitude
                        }
                    }
                    else{
                        Log.w(TAG, "not in SG!!!")
                        Toast.makeText(this@MainActivity, "You seem to be out of Singapore, so we will move your viewpoint to SG.", Toast.LENGTH_SHORT).show()
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(1.282302, 103.858528), 12f))
                        map.addMarker(MarkerOptions()
                                .position(LatLng(1.282302, 103.858528))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))

                        with(application as SassApp){
                            fixedCurrentLatitude = 1.282302
                            fixedCurrentLongitude = 103.858528
                        }
                    }
                    c += 1
                }
            }
        }

        fun isUserInsideSG(lat:Double, lng:Double):Boolean = (lat in SOUTHMOST_LAT..NORTHMOST_LAT && lng in WESTMOST_LNG..EASTMOST_LNG)

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

        override fun onProviderEnabled(provider: String?) {
            Log.i(TAG, "onProviderEnabled")
        }

        override fun onProviderDisabled(provider: String?) {
            Log.i(TAG, "onProviderDisabled")
        }
    }

    override fun onStart() {
        super.onStart()
        with(application as SassApp){
            if(fixedCurrentLatitude == 0.0 || fixedCurrentLongitude == 0.0){
                initLocationManager()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationMgr.removeUpdates(locationListener)
    }

    private fun checkPermission():Boolean{
        Log.w(TAG, "Requesting Permission")
        return when(ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.ACCESS_FINE_LOCATION)){
            PackageManager.PERMISSION_GRANTED->{
                true
            }
            else->{
                false
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 0x00){
            if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.w(TAG, "FINE LOCATION Permission Granted.")
            }
            else{
                Log.w(TAG, "FINE LOCATION Permission NOT Granted.")
                Snackbar.make(ll_booking_info, "Location Permission is needed.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK"){
                            ActivityCompat.requestPermissions(this@MainActivity,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    0x00)
                        }
                        .show()
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}