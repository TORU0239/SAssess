package my.com.toru.sassess

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import my.com.toru.sassess.remote.ApiHelper
import my.com.toru.sassess.remote.BookingApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ApiHelper.retrofit.create(BookingApi::class.java).getCarLocation()
                .enqueue(object: Callback<String> {
                    override fun onFailure(call: Call<String>, t: Throwable) {
                        Log.w("MainActivity", "failure")
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<String>, response: Response<String>) {
                        Log.w("MainActivity", "onResponse")
                    }
                })
    }
}
