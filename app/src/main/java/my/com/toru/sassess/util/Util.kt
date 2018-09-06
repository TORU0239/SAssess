package my.com.toru.sassess.util

import android.content.Context
import android.net.ConnectivityManager
import android.support.design.widget.Snackbar
import android.view.View
import my.com.toru.sassess.R

object Util {

    /* INFORMATION OF TERRITORY OF SINGAPORE */
    const val NORTHMOST_LAT = 1.470556
    const val NORTHMOST_LNG = 103.817222

    const val WESTMOST_LAT = 1.242538
    const val WESTMOST_LNG = 103.6047383

    const val SOUTHMOST_LAT = 1.238450
    const val SOUTHMOST_LNG = 103.832928

    const val EASTMOST_LAT = 1.349264
    const val EASTMOST_LNG = 104.043313

    /* INFORMATION OF DEFAULT LOCATION WHEN USER IS NOT IN SINGAPORE */
    const val MARINA_BAY_SANDS_LAT = 1.282302
    const val MARINA_BAY_SANDS_LNG =103.858528

    /* INTENT KEY FOR EXTRAS */
    const val DROP_OFF = "DROP_OFF"
    const val SELECTED_LAT = "SELECTED_LAT"
    const val SELECTED_LNG = "SELECTED_LNG"
    const val START_TS = "START_TS"
    const val END_TS = "END_TS"

    /* REQUEST ID */
    const val REQUEST_CODE = 0x00

    /* MISCELLANEOUS*/
    const val BOOKING_DIALOG_TAG = "booking_info"
    const val BOOKING_DIALOG_DATEFORMATTER = "yyyy-MM-dd HH:mm:ss"
    const val COUNTDOWN_MAX = 3
    const val COUNTDOWN_MAX_ARG = 0x39

    fun checkNetworkState(ctx: Context):Boolean {
        val connectivityMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityMgr.activeNetworkInfo
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting)
    }

    fun isUserInsideSG(lat:Double, lng:Double):Boolean = (lat in Util.SOUTHMOST_LAT..Util.NORTHMOST_LAT && lng in Util.WESTMOST_LNG..Util.EASTMOST_LNG)

    fun makePermissionSnackbar(container: View):Snackbar = Snackbar.make(container, R.string.need_location_permission, Snackbar.LENGTH_INDEFINITE)

}