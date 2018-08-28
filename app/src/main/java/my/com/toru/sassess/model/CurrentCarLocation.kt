package my.com.toru.sassess.model

import com.google.gson.annotations.SerializedName
import java.util.*

data class CurrentCarLocation(val data: LinkedList<EachCarLocation>)
data class EachCarLocation(val id:String,
                           val latitude:Float,
                           val longitude:Float,
                           @SerializedName("is_on_trip") val isOnTrip:Boolean)