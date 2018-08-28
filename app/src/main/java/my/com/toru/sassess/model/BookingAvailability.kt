package my.com.toru.sassess.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class RequestedBookingAvailability(val data:LinkedList<BookingAvailability>)
data class BookingAvailability(@SerializedName("dropoff_locations") val dropOffLocations: LinkedList<DropOffLocations>,
                               @SerializedName("available_cars") val availableCar:Int,
                               val id:Int,
                               val location:LinkedList<Float>)
data class DropOffLocations(val id:Int,
                            val location: LinkedList<Float>)
