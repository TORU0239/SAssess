package my.com.toru.sassess.remote

import my.com.toru.sassess.model.CurrentCarLocation
import my.com.toru.sassess.model.GeocodeInformation
import my.com.toru.sassess.model.RequestedBookingAvailability
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface BookingApi {

    @GET("/locations")
    fun getCurrentCarLocation():Call<CurrentCarLocation>

    @GET("/availability")
    fun getAvailability(@Query("startTime") startTime:Long,
                        @Query("endTime") endTime:Long):Call<RequestedBookingAvailability>


    @GET("https://maps.googleapis.com/maps/api/geocode/json")
    fun getAddress(@QueryMap query:HashMap<String, String>):Call<GeocodeInformation>
}