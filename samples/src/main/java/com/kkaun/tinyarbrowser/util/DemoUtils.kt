@file:JvmName("DemoUtils")
@file:JvmMultifileClass
package com.kkaun.tinyarbrowser.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.kkaun.mediator.ui.aug.framework.activity.ARActivity
import com.kkaun.tinyarbrowser.data.CacheDataSource
import com.kkaun.tinyarbrowser.paintables.ARMarker
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

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


fun convertTOsInMarkers(activity: ARActivity, markerTOs: List<ARMarkerTransferable>)
        : CopyOnWriteArrayList<ARMarker> {
    return CopyOnWriteArrayList(markerTOs.map {
        ARMarker(it.name, it.latitude, it.longitude, it.altitude.toDouble(), it.color,
                getBitmapFromDrawableNameForActivity(activity, it.bitmapName)) })
}


/**
 * Method for generating mock data based on current location for demonstrating purposes
 */
fun loadFreshMockData(userLocation: Location, markerTOs: ArrayList<ARMarkerTransferable>,
                      markersDataSource: CacheDataSource, activity: ARActivity) {
    val mockRadius = 5000
    val multiplyFactor = 1.0
    val step = 0.008
    val random = Random()

    val p1 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 0.toDouble())
    val p2 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 90.toDouble())
    val p3 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 180.toDouble())
    val p4 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 270.toDouble())

    val lonMin = p3.x.toDouble()
    val lonMax = p1.x.toDouble()
    val latMax = p2.y.toDouble()
    val latMin = p4.y.toDouble()

    var x = lonMin
    while (x <= lonMax) {
        var y = latMin
        while (y <= latMax) {
            markerTOs.add(ARMarkerTransferable("$x | $y", y, x, random.nextInt(100),
                    Color.rgb(255, 255, 255), "custom_marker_grey"))
            y += step
        }
        x += step
    }
    markersDataSource.setData(convertTOsInMarkers(activity, markerTOs))
}