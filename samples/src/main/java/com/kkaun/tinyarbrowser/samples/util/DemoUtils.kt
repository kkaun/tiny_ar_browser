@file:JvmName("DemoUtils")
@file:JvmMultifileClass
package com.kkaun.tinyarbrowser.samples.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PointF
import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.kkaun.tinyarbrowser.activity.ARActivity
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
    return CopyOnWriteArrayList(markerTOs.map { mapARMarkerTOInARMarker(activity, it) })
}



fun mapARMarkerTOInARMarker(activity: ARActivity, arMarkerTO: ARMarkerTransferable): ARMarker {

    val m = ARMarker(arMarkerTO.name, arMarkerTO.description, arMarkerTO.latitude, arMarkerTO.longitude,
            arMarkerTO.altitude.toDouble(), arMarkerTO.color,
            getBitmapFromDrawableNameForActivity(activity, arMarkerTO.bitmapName))

    // Uncomment methods below to test marker text box customization
    //m.setBodyColor(250, 218, 76, 76)
    //m.setFontColor(76, 218, 71)
    //m.setFrameColor(245, 230, 96)
    return m
}


/**
 * Method for generating mock data based on current location for demonstrating purposes
 */
fun getFreshMockData(userLocation: Location): ArrayList<ARMarkerTransferable> {

    val markerTOs = ArrayList<ARMarkerTransferable>()
    val mockRadius = 3000
    val multiplyFactor = 1.0
    val step = 0.01
    val random = Random()

    val p1 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 0.toDouble())
    val p2 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 90.toDouble())
    val p3 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 180.toDouble())
    val p4 = calculateDerivedPosition(convertLocationToPointF(userLocation),
            multiplyFactor * mockRadius, 270.toDouble())

    //		//For Saint-Petersburg area
//		double latMin = 59.839738;
//		double latMax = 60.003991;
//		double lonMin = 30.236244;
//		double lonMax = 30.474154;

    //For Kramators'k area
    val latMin = 48.674679
    val latMax = 48.811720
    val lonMin = 37.453154
    val lonMax = 37.658511

    var nameCounter = 0
    var x = lonMin
    while (x <= lonMax) {
        var y = latMin
        while (y <= latMax) {
            markerTOs.add(ARMarkerTransferable("Random Marker $nameCounter", "Description!!!!!",
                    y, x, random.nextInt(50), Color.rgb(255, 255, 255),
                    "custom_marker_grey"))
            y += step
            nameCounter++
        }
        x += step
    }
    return markerTOs
}