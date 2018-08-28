package my.com.toru.sassess.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import kotlinx.android.synthetic.main.activity_main.*
import my.com.toru.sassess.R
import my.com.toru.sassess.remote.ApiHelper

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG:String = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fab_refresh.setOnClickListener {
            ApiHelper.getCurrentBookableCar(1535709600, 1535796000, successCB = {res ->
                Log.w(TAG, "size:: ${res.body()?.data?.size}")
            }, failedCB = {
                Log.w(TAG, "WTF!!!")
            })
        }
    }
}