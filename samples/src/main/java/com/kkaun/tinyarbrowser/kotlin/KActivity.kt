package com.kkaun.tinyarbrowser.kotlin

import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.kkaun.mediator.ui.aug.framework.activity.ARActivity
import com.kkaun.mediator.ui.aug.framework.data.ARDataRepository
import com.kkaun.mediator.ui.aug.framework.data.CacheDataSource
import com.kkaun.mediator.ui.aug.framework.paintables.ARMarker
import com.kkaun.mediator.ui.aug.framework.paintables.Marker
import com.kkaun.tinyarbrowser.util.ARMarkerTransferable
import com.kkaun.tinyarbrowser.util.calculateDerivedPosition
import com.kkaun.tinyarbrowser.util.convertLocationToPointF
import com.kkaun.tinyarbrowser.util.getBitmapFromDrawableNameForActivity
import java.util.*
import java.util.concurrent.*


/**
 * Created by Кира on 28.03.2018.
 */

class KActivity : ARActivity() {

    companion object {
        private val TAG = "ARBrowserActivity"
        private val executorService = ThreadPoolExecutor(1, 1,
                20, TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(1))
    }
    lateinit var markerTOs: ArrayList<ARMarkerTransferable>
    val markersDataSource: CacheDataSource = CacheDataSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        markerTOs = ArrayList()

        setRadarBodyColor(200, 138, 138, 138)
        setRadarLineColor(255, 255, 255)
        setRadarBodyRadius(100)
        setRadarTextColor(255, 255, 255)

        if (savedInstanceState != null) getExtraData(savedInstanceState)
        else getExtraData(intent.extras)
        ARDataRepository.addMarkers(markersDataSource.markersCache as List<Marker>)
    }

    fun getExtraData(extras: Bundle) {
        if(extras.containsKey("place_ar_markers")){
            markerTOs = extras.getParcelableArrayList("place_ar_markers")
            markersDataSource.setData(CopyOnWriteArrayList(markerTOs.map {
                ARMarker(it.name, it.latitude, it.longitude, it.altitude.toDouble(), it.color,
                        getBitmapFromDrawableNameForActivity(this@KActivity, it.bitmapName)) })) }
    }

    override fun onLocationChanged(location: Location) {
        super.onLocationChanged(location)
        updateData(location)
    }

    override fun markerTouched(marker: Marker) {
        val t = Toast.makeText(applicationContext, marker.name, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER, 0, 0)
        t.show()
    }

    /**
     * Location retrieving mechanism can be replaced by any side location library or your own code
     */
    override fun updateDataOnZoom() {
        super.updateDataOnZoom()
        val lastLocation = ARDataRepository.getCurrentLocation()
        updateData(lastLocation)
    }

    private fun updateData(lastLocation: Location) {
        try { executorService.execute { loadFreshMockData(lastLocation) }
        } catch (rej: RejectedExecutionException) {
            Log.w(TAG, "Not running new download Runnable, queue is full.")
        } catch (e: Exception) {
            Log.e(TAG, "Exception running download Runnable.", e)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("place_ar_markers", markerTOs)
    }

    /**
     * Method for generating mock data based on current location for demonstrating purposes
     */
    fun loadFreshMockData(userLocation: Location) {

        markerTOs = ArrayList()
        val mockRadius = 5000
        val multiplyFactor = 1.0
        val step = 0.009
        val random = Random()

        val p1 = calculateDerivedPosition(convertLocationToPointF(userLocation),
                multiplyFactor * mockRadius, 0.toDouble())
        val p2 = calculateDerivedPosition(convertLocationToPointF(userLocation),
                multiplyFactor * mockRadius, 90.toDouble())
        val p3 = calculateDerivedPosition(convertLocationToPointF(userLocation),
                multiplyFactor * mockRadius, 180.toDouble())
        val p4 = calculateDerivedPosition(convertLocationToPointF(userLocation),
                multiplyFactor * mockRadius, 270.toDouble())

        //val arr = arrayOf<PointF>(p1, p2, p3, p4)
        //arr[2].x, arr[0].x, arr[1].y, arr[3].y) !!!!!!!
        val lonMin = p3.x
        val lonMax = p1.x
        val latMin = p2.y
        val latMax = p4.y

        var x = lonMin.toDouble()
        while (x <= lonMax) {
            var y = latMin.toDouble()
            while (y <= latMax) {
                markerTOs.add(ARMarkerTransferable("$x | $y", y, x, random.nextInt(100),
                        Color.rgb(255, 255, 255), "custom_marker_grey"))
                y += step
            }
            x += step
        }
        markersDataSource.setData(CopyOnWriteArrayList(markerTOs.map {
            ARMarker(it.name, it.latitude, it.longitude, it.altitude.toDouble(), it.color,
                    getBitmapFromDrawableNameForActivity(this@KActivity, it.bitmapName)) }))
    }
}


