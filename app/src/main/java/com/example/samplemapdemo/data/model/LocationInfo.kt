package com.example.samplemapdemo.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(primaryKeys = ["lat", "lng"])
data class LocationInfo (
    @SerializedName("name") val name : String = "",

    @SerializedName("lat") val lat : Double,

    @SerializedName("lng") val lng : Double,

    @Expose(serialize = false, deserialize = false)
    var description: String?,

    @Expose(serialize = false, deserialize = false)
    var userAddedLocation: Boolean = false,

    @Expose(serialize = false, deserialize = false)
    var distanceLocal: Long = -1

) : Parcelable

data class LocationResponse (

    @SerializedName("locations") val locations : List<LocationInfo>,
    val updated : String
)