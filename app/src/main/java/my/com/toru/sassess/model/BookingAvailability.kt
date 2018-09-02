package my.com.toru.sassess.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class RequestedBookingAvailability(val data:LinkedList<BookingAvailability>):Serializable
data class BookingAvailability(@SerializedName("dropoff_locations") val dropOffLocations: ArrayList<DropOffLocations>,
                               @SerializedName("available_cars") val availableCar:Int,
                               val id:Int,
                               val location:ArrayList<Double>):Serializable
data class DropOffLocations(val id:Int,
                            val location: ArrayList<Double>):Serializable
