package my.com.toru.sassess.ui.presenter

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import my.com.toru.sassess.R
import my.com.toru.sassess.remote.ApiHelper
import my.com.toru.sassess.ui.MainActivity
import my.com.toru.sassess.ui.view.MainView
import my.com.toru.sassess.util.Util

class MainPresenterImp(val view: MainView) : MainPresenter{


    override fun requestPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale((view.getViewContext() as MainActivity), Manifest.permission.ACCESS_FINE_LOCATION)){
            view.showPermissionSnackbar()
        }
        else{
            ActivityCompat.requestPermissions((view.getViewContext() as MainActivity), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 0x00)
        }
    }

    override fun checkPermissionGranted(): Boolean
        = when(ActivityCompat.checkSelfPermission((view.getViewContext() as Activity), Manifest.permission.ACCESS_FINE_LOCATION)){
            PackageManager.PERMISSION_GRANTED -> true
            else -> false
        }

    override fun requestPickupPoint(start: Long, end: Long) {
        if(Util.checkNetworkState(view.getViewContext())){
            view.showOrHideProgress(true)
            ApiHelper.getCurrentBookableCar(start, end, successCB = {
                response->
                view.showOrHideProgress(false)
                response.body()?.data?.let { list ->
                    if (list.size > 0) {
                        view.showLocationPicker(list)
                    }
                }
            }, failedCB = {
                view.showOrHideProgress(false)
                view.showSnackbar(R.string.network_error)
            })
        }
        else{
            view.showSnackbar(R.string.check_internet_connection)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray): Boolean {
        if(requestCode == Util.REQUEST_CODE){
            if(grantResults.size != 1 || grantResults[0] == PackageManager.PERMISSION_GRANTED){
                view.showPermissionSnackbar()
            }
            return true
        }
        else{
            return false
        }
    }
}