package my.com.toru.sassess.ui.view

import android.content.Context
import my.com.toru.sassess.model.BookingAvailability

interface BaseView{
    fun getViewContext(): Context
}

interface LaunchView:BaseView{
    fun showPermissionSnackbar()
}

interface MainView:BaseView{
    fun showOrHideProgress(show:Boolean)
    fun showPermissionSnackbar()
    fun showLocationPicker(list:List<BookingAvailability>)
    fun showSnackbar(strRes:Int)
}