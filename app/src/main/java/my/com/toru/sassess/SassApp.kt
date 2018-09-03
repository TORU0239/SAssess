package my.com.toru.sassess

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

class SassApp: Application(){

    var fixedCurrentLatitude:Double = 0.0
    var fixedCurrentLongitude:Double = 0.0

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}