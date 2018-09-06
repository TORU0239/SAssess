package my.com.toru.sassess.ui.presenter

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import android.util.Log
import my.com.toru.sassess.ui.LaunchActivity
import my.com.toru.sassess.ui.MainActivity
import my.com.toru.sassess.ui.view.LaunchPresenter
import my.com.toru.sassess.ui.view.LaunchView
import my.com.toru.sassess.util.Util

class LaunchPresenterImp(val view:LaunchView) : LaunchPresenter{

    override fun checkPermission() {
        val act = view.getViewContext() as Activity
        if(ActivityCompat.shouldShowRequestPermissionRationale(act, Manifest.permission.ACCESS_FINE_LOCATION)){
            view.showPermissionSnackbar()
        }
        else{
            ActivityCompat.requestPermissions(act, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Util.REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean{
        if(requestCode == Util.REQUEST_CODE){
            return if(grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.w(LaunchActivity.TAG, "FINE LOCATION Permission Granted.")
                (view.getViewContext() as Activity).apply {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                true
            } else{
                Log.w(LaunchActivity.TAG, "FINE LOCATION Permission NOT Granted.")
                view.showPermissionSnackbar()
                true
            }
        }
        else{
            return false
        }
    }
}