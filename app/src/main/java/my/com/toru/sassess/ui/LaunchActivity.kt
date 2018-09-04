package my.com.toru.sassess.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_launch.*
import my.com.toru.sassess.R
import my.com.toru.sassess.SassApp


class LaunchActivity : AppCompatActivity() {

    companion object {
        val TAG = LaunchActivity::class.java.simpleName!!
        val REQUEST_CODE = 0x00
    }

    private lateinit var ctx: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        ctx = this@LaunchActivity

        initializeSavedLocation()
        checkPermission()
    }

    private fun initializeSavedLocation(){
        with(application as SassApp){
            fixedCurrentLatitude = 0.0
            fixedCurrentLongitude = 0.0
        }
    }

    private fun checkPermission(){
        Log.w(TAG, "Requesting Permission")
        if(ActivityCompat.shouldShowRequestPermissionRationale(ctx, Manifest.permission.ACCESS_FINE_LOCATION)){
            Snackbar.make(container, "Location Permission is needed.",
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK"){
                        ActivityCompat.requestPermissions(ctx,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                0x00)
                    }
                    .show()
        }
        else{
            ActivityCompat.requestPermissions(ctx, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == 0x00){
            if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.w(TAG, "FINE LOCATION Permission Granted.")
                startActivity(Intent(ctx, MainActivity::class.java))
                finish()
            }
            else{
                Log.w(TAG, "FINE LOCATION Permission NOT Granted.")
                Snackbar.make(container, "Location Permission is needed.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK"){
                            ActivityCompat.requestPermissions(ctx,
                                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                    REQUEST_CODE)
                        }
                        .show()
            }
        }
        else{
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}