package com.kkaun.tinyarbrowser.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.kkaun.mediator.ui.aug.framework.activity.ARActivity

/**
 * Created by Кира on 29.03.2018.
 */


fun calculateDerivedPosition(point: PointF, range: Double, bearing: Double): PointF {

    val latA = Math.toRadians(point.x.toDouble())
    val lonA = Math.toRadians(point.y.toDouble())
    val angularDistance = range / 6371000
    val trueCourse = Math.toRadians(bearing)

    var lat = Math.asin(Math.sin(latA) * Math.cos(angularDistance) + Math.cos(latA)
            * Math.sin(angularDistance) * Math.cos(trueCourse))
    val dlon = Math.atan2(Math.sin(trueCourse) * Math.sin(angularDistance) * Math.cos(latA),
            Math.cos(angularDistance) - Math.sin(latA) * Math.sin(lat))

    var lon = (lonA + dlon + Math.PI) % (Math.PI * 2) - Math.PI

    lat = Math.toDegrees(lat)
    lon = Math.toDegrees(lon)

    return PointF(lat.toFloat(), lon.toFloat())
}


fun convertLocationToPointF(location: Location): PointF
        = PointF(location.latitude.toFloat(), location.longitude.toFloat())


fun getBitmapFromDrawableNameForActivity(activity: ARActivity, drawableName: String): Bitmap {
    val resId = activity.resources.getIdentifier(drawableName, "drawable", activity.packageName)
    return BitmapFactory.decodeResource(activity.resources, resId)
}


inline fun <reified T : Parcelable> createParcel(
        crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }