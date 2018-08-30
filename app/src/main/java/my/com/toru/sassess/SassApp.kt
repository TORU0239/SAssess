package my.com.toru.sassess

import android.app.Application
import android.content.Context
import com.facebook.stetho.Stetho

class SassApp: Application(){
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }
}