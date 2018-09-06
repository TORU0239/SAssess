package my.com.toru.sassess.ui.view

import android.content.Context

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
    fun showPermissionSnackbar()
    fun showLocationPicker()
}

interface MainPresenter:BasePresenter{
    fun requestPickupPoint()
    fun checkPermission():Boolean
}