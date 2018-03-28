package com.kkaun.mediator.ui.aug.framework.data

import android.location.Location
import android.location.LocationManager
import android.util.Log
import com.kkaun.mediator.ui.aug.framework.paintables.Marker
import com.kkaun.mediator.ui.aug.framework.projection.Matrix
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

object ARDataRepository {

    private val TAG = "ARDataRepository"
    private val markerMap = ConcurrentHashMap<String?, Marker>()
    private val cache = CopyOnWriteArrayList<Marker>()
    private val dirty = AtomicBoolean(false)//????
    private val locationArray = FloatArray(3)
    val hardFix = Location(LocationManager.GPS_PROVIDER)//???
    private val radiusLock = Any()
    private var radius = 20f
    private var zoomLevel = ""
    private val zoomProgressLock = Any()
    private var zoomProgress = 0
    private var currentLocation = hardFix
    private var rotationMatrix = Matrix()
    private val azimuthLock = Any()
    private var azimuth = 0f
    private val pitchLock = Any()
    private var pitch = 0f
    private val rollLock = Any()
    private var roll = 0f

    val markers: List<Marker>
        get() {
            if (dirty.compareAndSet(true, false)) {
                Log.v(TAG, "DIRTY flag found, resetting all marker heights to zero.")
                for (marker in markerMap.values) {
                    marker.location[locationArray]
                    locationArray[1] = marker.initialY
                    marker.location.set(locationArray)
                }
                Log.v(TAG, "Populating the cache.")
                val copy = ArrayList<Marker>()
                copy.addAll(markerMap.values)
                Collections.sort(copy, comparator)
                cache.clear()
                cache.addAll(copy)
            }
            return Collections.unmodifiableList(cache)
        }

    private val comparator = Comparator<Marker> {
        arg0, arg1 -> java.lang.Double.compare(arg0.distance, arg1.distance) }

    init {
        hardFix.latitude = 0.0
        hardFix.longitude = 0.0
        hardFix.altitude = 1.0
    }

    fun setZoomLevel(zoomLevel: String?) {
        if (zoomLevel == null) throw NullPointerException()
        synchronized(ARDataRepository.zoomLevel) { ARDataRepository.zoomLevel = zoomLevel }
    }

    fun setZoomProgress(zoomProgress: Int) {
        synchronized(ARDataRepository.zoomProgressLock) {
            if (ARDataRepository.zoomProgress != zoomProgress) {
                ARDataRepository.zoomProgress = zoomProgress
                if (dirty.compareAndSet(false, true)) {
                    Log.v(TAG, "Setting DIRTY flag!")
                    cache.clear()
                }
            }
        }
    }

    fun setRadius(radius: Float) {
        synchronized(ARDataRepository.radiusLock) { ARDataRepository.radius = radius }
    }
    fun getRadius(): Float {
        synchronized(ARDataRepository.radiusLock) { return ARDataRepository.radius }
    }

    fun setCurrentLocation(currentLocation: Location?) {
        if (currentLocation == null) throw NullPointerException()
        Log.d(TAG, "current location. location=" + currentLocation.toString())
        synchronized(currentLocation) { ARDataRepository.currentLocation = currentLocation }
        onLocationChanged(currentLocation)
    }

    fun getCurrentLocation(): Location {
        synchronized(ARDataRepository.currentLocation) { return ARDataRepository.currentLocation }
    }

    fun setRotationMatrix(rotationMatrix: Matrix) {
        synchronized(ARDataRepository.rotationMatrix) { ARDataRepository.rotationMatrix = rotationMatrix }
    }

    fun getRotationMatrix(): Matrix {
        synchronized(ARDataRepository.rotationMatrix) { return rotationMatrix }
    }

    fun addMarkers(markers: Collection<Marker>?) {
        if (markers == null) throw NullPointerException()
        if (markers.isEmpty()) return
        Log.d(TAG, "New markers, updating markers. New markers: " + markers.toString())
        for (marker in markers) {
            if (!markerMap.containsKey(marker.name)) {
                marker.calcRelativePosition(ARDataRepository.getCurrentLocation())
                markerMap[marker.name] = marker
            }
        }
        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!")
            cache.clear()
        }
    }

    private fun onLocationChanged(location: Location) {
        Log.d(TAG, "New location, updating markers. location=" + location.toString())
        for (ma in markerMap.values) { ma.calcRelativePosition(location) }
        if (dirty.compareAndSet(false, true)) {
            Log.v(TAG, "Setting DIRTY flag!")
            cache.clear()
        }
    }

    fun setAzimuth(azimuth: Float) {
        synchronized(azimuthLock) { ARDataRepository.azimuth = azimuth }
    }
    fun getAzimuth(): Float {
        synchronized(azimuthLock) { return ARDataRepository.azimuth }
    }
    fun setPitch(pitch: Float) {
        synchronized(pitchLock) { ARDataRepository.pitch = pitch }
    }
    fun getPitch(): Float {
        synchronized(pitchLock) { return ARDataRepository.pitch }
    }
    fun setRoll(roll: Float) {
        synchronized(rollLock) { ARDataRepository.roll = roll }
    }
    fun getRoll(): Float {
        synchronized(rollLock) { return ARDataRepository.roll }
    }
}