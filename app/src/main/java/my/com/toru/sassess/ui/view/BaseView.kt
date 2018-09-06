package my.com.toru.sassess.ui.view

import android.content.Context
import my.com.toru.sassess.model.BookingAvailability

interface BaseView{
    fun getViewContext(): Context
}
interface BasePresenter

interface LaunchView:BaseView{
    fun showPermissionSnackbar()
}

interface LaunchPresenter:BasePresenter{
    fun checkPermission()
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean
}

interface MainView:BaseView{
    fun showOrHideProgress(show:Boolean)
    fun showPermissionSnackbar()
    fun showLocationPicker(list:List<BookingAvailability>)
    fun showSnackbar(strRes:Int)
}

interface MainPresenter:BasePresenter{
    fun requestPermission()
    fun checkPermissionGranted():Boolean
    fun requestPickupPoint(start:Long, end:Long)
    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray):Boolean
}