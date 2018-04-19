package com.kkaun.tinyarbrowser.samples.util

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

/**
 * Created by Кира on 29.03.2018.
 */

class ARMarkerTransferable(val name: String,
                 val latitude: Double,
                 val longitude: Double,
                 val altitude: Int,
                 val color: Int,
                 val bitmapName: String): Serializable, Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { ARMarkerTransferable(it) }
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readDouble(),
            parcel.readDouble(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeDouble(latitude)
        dest.writeDouble(longitude)
        dest.writeInt(altitude)
        dest.writeInt(color)
        dest.writeString(bitmapName)
    }

    override fun describeContents(): Int = 0
}