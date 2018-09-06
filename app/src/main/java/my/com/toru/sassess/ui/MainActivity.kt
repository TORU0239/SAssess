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
import my.com.toru.sassess.util.Util
import my.com.toru.sassess.util.Util.isUserInsideSG
import my.com.toru.sassess.util.actionAndRequestPermission
import my.com.toru.sassess.util.distance
import my.com.toru.sassess.util.generateMarker
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    companion object {
        private const val TAG:String = "MainActivity"
        private const val COUNT = 1
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
                                    .generateMarker((application as SassApp).fixedCurrentLatitude, (application as SassApp).fixedCurrentLongitude,
                                            BitmapDescriptorFactory.HUE_ORANGE))


                            // making and adding marker on Google Map Fragment
                            for(eachItem in list){
                                val options = MarkerOptions()
                                        .position(LatLng(eachItem.location[0], eachItem.location[1]))
                                        .title("Available cars: ${eachItem.availableCar}")
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
                setOnMarkerClickListener(this@MainActivity)
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
                    Snackbar.make(ll_booking_info, R.string.need_location_permission, Snackbar.LENGTH_INDEFINITE)
                            .actionAndRequestPermission(this@MainActivity)
                }
                else{
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0x00)
                }
            }
        }
    }

    override fun onInfoWindowClick(marker: Marker?) {
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
                                .putExtra(Util.DROP_OFF, list)
                                .putExtra(Util.SELECTED_LAT, tag.location[0])
                                .putExtra(Util.SELECTED_LNG, tag.location[1])
                                .putExtra(Util.START_TS, (calendar.timeInMillis))
                                .putExtra(Util.END_TS, (secondCalendar.timeInMillis))

                        startActivity(intent)
                        marker.hideInfoWindow()
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
                        map.addMarker(MarkerOptions().generateMarker(it.latitude, it.longitude, BitmapDescriptorFactory.HUE_ORANGE))
                        with(application as SassApp){
                            fixedCurrentLatitude = location.latitude
                            fixedCurrentLongitude = location.longitude
                        }
                    }
                    else{
                        Log.w(TAG, "not in SG!!!")
                        Toast.makeText(this@MainActivity, R.string.user_is_out_of_sg, Toast.LENGTH_SHORT).show()
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(Util.MARINA_BAY_SANDS_LAT, Util.MARINA_BAY_SANDS_LNG), 12f))
                        map.addMarker(MarkerOptions()
                                .position(LatLng(1.282302, 103.858528))
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)))

                        with(application as SassApp){
                            fixedCurrentLatitude = Util.MARINA_BAY_SANDS_LAT
                            fixedCurrentLongitude = Util.MARINA_BAY_SANDS_LAT
                        }
                    }
                    c += 1
                }
            }
        }

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
                Util.makePermissionSnackbar(ll_booking_info).actionAndRequestPermission(this@MainActivity)
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val lat = (marker.tag as BookingAvailability).location[0]
        val lng = (marker.tag as BookingAvailability).location[1]

        val currentLatitude = (application as SassApp).fixedCurrentLatitude
        val currentLongitude= (application as SassApp).fixedCurrentLongitude

        val distanceFromUser = getString(R.string.distance_from_user)
        FloatArray(2).let {
            Location.distanceBetween(currentLatitude, currentLongitude, lat, lng, it)
            pickup_txt.text = distanceFromUser.distance(it[0])
        }

        return false
    }
}