package my.com.toru.sassess

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import my.com.toru.sassess.model.BookingAvailability
import my.com.toru.sassess.model.CurrentCarLocation
import my.com.toru.sassess.model.RequestedBookingAvailability
import my.com.toru.sassess.remote.ApiHelper
import my.com.toru.sassess.remote.BookingApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        ApiHelper.retrofit.create(BookingApi::class.java).getCurrentCarLocation()
//                .enqueue(object: Callback<CurrentCarLocation> {
//                    override fun onFailure(call: Call<CurrentCarLocation>, t: Throwable) {
//                        Log.w("MainActivity", "failure")
//                        t.printStackTrace()
//                    }
//
//                    override fun onResponse(call: Call<CurrentCarLocation>, response: Response<CurrentCarLocation>) {
//                        when(response.code()){
//                            200 -> {
//                                Log.w("MainActivity", "size:: ${response.body()?.data?.size}")
//                            }
//                            else ->{
//                                Log.w("MainActivity", "WTF!!!")
//                            }
//                        }
//                    }
//                })

        ApiHelper.retrofit.create(BookingApi::class.java).getAvailability(1535709600, 1535796000)
                .enqueue(object:Callback<RequestedBookingAvailability>{
                    override fun onFailure(call: Call<RequestedBookingAvailability>, t: Throwable) {
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<RequestedBookingAvailability>, response: Response<RequestedBookingAvailability>) {
                        when(response.code()){
                            200 -> {
                                Log.w("MainActivity", "size:: ${response.body()?.data?.size}")
                            }
                            else ->{
                                Log.w("MainActivity", "WTF!!!")
                            }
                        }
                    }
                })
    }
}
