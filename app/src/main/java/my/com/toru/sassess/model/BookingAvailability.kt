package my.com.toru.sassess.model

import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import java.io.Serializable
import java.util.*

data class RequestedBookingAvailability(val data:LinkedList<BookingAvailability>):Serializable
data class BookingAvailability(@SerializedName("dropoff_locations") val dropOffLocations: ArrayList<DropOffLocations>,
                               @SerializedName("available_cars") val availableCar:Int,
                               val id:Int,
                               val location:ArrayList<Double>):Serializable

data class DropOffLocations(val id:Int,
                            val location: ArrayList<Double>):Serializable

data class GeocodeInformation(val results:ArrayList<GeocodeComponent>,
                              val status:String):Serializable

data class GeocodeComponent(@SerializedName("address_components") val addressComponents:ArrayList<AddressComponent>,
                            @SerializedName("formatted_address") val formattedAddress:String,
                            @SerializedName("place_id") val placeId:String,
                            val types:ArrayList<String>)

data class AddressComponent(@SerializedName("long_name") val longName:String,
                            @SerializedName("short_name") val shortName:String,
                            val types:ArrayList<String>):Serializable

