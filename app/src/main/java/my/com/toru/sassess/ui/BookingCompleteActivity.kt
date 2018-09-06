package my.com.toru.sassess.ui

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import my.com.toru.sassess.R

class BookingCompleteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_booking_complete)
        Handler().postDelayed({
            setResult(Activity.RESULT_OK)
            finish()
        }, 2000)
    }
}
