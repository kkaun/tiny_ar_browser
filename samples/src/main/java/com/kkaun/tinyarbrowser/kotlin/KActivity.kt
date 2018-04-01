package com.kkaun.tinyarbrowser.kotlin

import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import com.kkaun.mediator.ui.aug.framework.activity.ARActivity
import com.kkaun.mediator.ui.aug.framework.data.ARDataRepository
import com.kkaun.mediator.ui.aug.framework.data.CacheDataSource
import com.kkaun.mediator.ui.aug.framework.paintables.Marker
import com.kkaun.tinyarbrowser.util.ARMarkerTransferable
import com.kkaun.tinyarbrowser.util.convertTOsInMarkers
import com.kkaun.tinyarbrowser.util.loadFreshMockData
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Created by Кира on 28.03.2018.
 */

class KActivity : ARActivity() {

    companion object {
        private val TAG = "KActivity"
        private val executorService = ThreadPoolExecutor(1, 1,
                20, TimeUnit.SECONDS, ArrayBlockingQueue<Runnable>(1))
        private val markersDataSource: CacheDataSource = CacheDataSource()
    }
    private lateinit var markerTOs: ArrayList<ARMarkerTransferable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        markerTOs = ArrayList()
        showProgressBar()

        setRadarBodyColor(200, 138, 138, 138)
        setRadarLineColor(255, 255, 255)
        setRadarBodyRadius(100)
        setRadarTextColor(255, 255, 255)

        useCollisionDetection = false

        if (savedInstanceState != null) getExtraData(savedInstanceState)
        else if(intent.extras != null) getExtraData(intent.extras)
        //FILL MOCK DATA
        ARDataRepository.addMarkers(markersDataSource.markersCache as List<Marker>)
    }

    private fun getExtraData(extras: Bundle) {
        if(extras.containsKey("place_ar_markers")){
            markerTOs = extras.getParcelableArrayList("place_ar_markers")
            markersDataSource.setData(convertTOsInMarkers(this@KActivity, markerTOs)) }
    }

    /**
     * Location retrieving mechanism can be replaced by any side location library or your own code
     */
    override fun onLocationChanged(location: Location) {
        super.onLocationChanged(location)
        updateData(location)
    }

    /**
     * Provide your own implementation of marker's click if needed
     */
    override fun onMarkerTouched(marker: Marker) {
        super.onMarkerTouched(marker)
        val t = Toast.makeText(applicationContext, "Clicked ${marker.name}", Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER, 0, 0)
        t.show()
    }

    override fun updateDataOnZoom() {
        super.updateDataOnZoom()
        val lastLocation = ARDataRepository.getCurrentLocation()
        updateData(lastLocation)
    }

    private fun updateData(lastLocation: Location) {
        try { executorService.execute {
            markerTOs = ArrayList()
            loadFreshMockData(lastLocation, markerTOs, markersDataSource, this@KActivity) }
            hideProgressBar()
        } catch (rej: RejectedExecutionException) {
            Log.w(TAG, "Not running new download Runnable, queue is full.")
        } catch (e: Exception) {
            Log.e(TAG, "Exception running download Runnable.", e) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArrayList("place_ar_markers", markerTOs)
    }
}


