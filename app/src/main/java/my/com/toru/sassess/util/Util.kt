package my.com.toru.sassess.util

import android.content.Context
import android.net.ConnectivityManager

object Util {

    /* INFORMATION OF TERRITORY OF SINGAPORE */
    val NORTHMOST_LAT = 1.470556
    val NORTHMOST_LNG = 103.817222

    val WESTMOST_LAT = 1.242538
    val WESTMOST_LNG = 103.6047383

    val SOUTHMOST_LAT = 1.238450
    val SOUTHMOST_LNG = 103.832928

    val EASTMOST_LAT = 1.349264
    val EASTMOST_LNG = 104.043313

    /* INFORMATION OF DEFAULT LOCATION WHEN USER IS NOT IN SINGAPORE */
    val MARINA_BAY_SANDS_LAT = 1.282302
    val MARINA_BAY_SANDS_LNG =103.858528

    fun checkNetworkState(ctx: Context):Boolean {
        val connectivityMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityMgr.activeNetworkInfo
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting)
    }

    fun isUserInsideSG(lat:Double, lng:Double):Boolean = (lat in Util.SOUTHMOST_LAT..Util.NORTHMOST_LAT && lng in Util.WESTMOST_LNG..Util.EASTMOST_LNG)
}