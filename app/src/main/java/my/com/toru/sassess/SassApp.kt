package my.com.toru.sassess

import android.app.Application
import com.facebook.stetho.Stetho

class SassApp: Application(){

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}