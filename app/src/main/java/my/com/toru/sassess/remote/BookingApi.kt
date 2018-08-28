package my.com.toru.sassess.remote

import retrofit2.Call
import retrofit2.http.GET

interface BookingApi {

    @GET("/locations")
    fun getCarLocation():Call<String>
}