package my.com.toru.sassess.remote

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import my.com.toru.sassess.model.CurrentCarLocation
import my.com.toru.sassess.model.RequestedBookingAvailability
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/*
* Singleton Pattern
* */
object ApiHelper {
    private const val BASE_URL = "https://challenge.smove.sg"
    private const val TIMEOUT = 5000L

    var retrofit:Retrofit

    init {
        Log.w("ApiHelper", "init")
        val networkInterceptor = HttpLoggingInterceptor()
        networkInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient= OkHttpClient.Builder()
                .addNetworkInterceptor(StethoInterceptor())
                .addInterceptor(networkInterceptor)
                .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                .build()

         retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
    }

    fun getCurrentBookableCar(start:Long,
                              end:Long,
                              successCB:(Response<RequestedBookingAvailability>)->Unit,
                              failedCB:()->Unit){
        retrofit.create(BookingApi::class.java).getAvailability(start, end)
                .enqueue(object:AdvCallback<RequestedBookingAvailability>(failedCB, successCB){})
    }

    fun getRealtimeCarLocation(callback:(Response<CurrentCarLocation>)->Unit){
        retrofit.create(BookingApi::class.java).getCurrentCarLocation().enqueue(object:Callback<CurrentCarLocation>{
            override fun onFailure(call: Call<CurrentCarLocation>, t: Throwable) {}

            override fun onResponse(call: Call<CurrentCarLocation>, response: Response<CurrentCarLocation>) {
                callback(response)
            }
        })
    }
}