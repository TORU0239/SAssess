package my.com.toru.sassess.remote

import android.content.Context
import android.net.ConnectivityManager

object Util {
    fun checkNetworkState(ctx: Context):Boolean {
        val connectivityMgr = ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityMgr.activeNetworkInfo
        return (activeNetwork != null && activeNetwork.isConnectedOrConnecting)
    }
}