package com.kkaun.mediator.ui.aug.framework.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.hardware.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.kkaun.tinyarbrowser.data.ARDataRepository
import com.kkaun.tinyarbrowser.projection.LowPassFilter
import com.kkaun.tinyarbrowser.projection.Matrix
import java.util.concurrent.atomic.AtomicBoolean

abstract class OrientationActivity : Activity(), SensorEventListener, LocationListener {

    companion object {
        private val TAG = "OrientationActivity"
        private val computing = AtomicBoolean(false)
        private val MIN_TIME = 30 * 1000
        private val MIN_DISTANCE = 10
        private val temp = FloatArray(9)
        private val rotation = FloatArray(9)
        private val gravity = FloatArray(3)
        private val mag = FloatArray(3)
        private var smooth = FloatArray(3)
        private val worldCoords = Matrix()
        private val magneticCompensatedCoord = Matrix()
        private val xAxisRotation = Matrix()
        private val magneticNorthCompensation = Matrix()
    }
    private var gmf: GeomagneticField? = null
    private var sensorMgr: SensorManager? = null
    private var sensors: List<Sensor>? = null
    private var sensorGrav: Sensor? = null
    private var sensorMag: Sensor? = null
    private var locationMgr: LocationManager? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingPermission")
    public override fun onStart() {
        super.onStart()
        val angleX = Math.toRadians(-90.0)
        var angleY = Math.toRadians(-90.0)
        xAxisRotation.set(1f, 0f, 0f, 0f,
                Math.cos(angleX).toFloat(), (-Math.sin(angleX)).toFloat(), 0f,
                Math.sin(angleX).toFloat(), Math.cos(angleX).toFloat())
        try {
            sensorMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            sensors = sensorMgr!!.getSensorList(Sensor.TYPE_ACCELEROMETER)
            if (sensors!!.isNotEmpty()) { sensorGrav = sensors!![0] }
            sensors = sensorMgr!!.getSensorList(Sensor.TYPE_MAGNETIC_FIELD)
            if (sensors!!.isNotEmpty()) { sensorMag = sensors!![0] }
            sensorMgr!!.registerListener(this, sensorGrav, SensorManager.SENSOR_DELAY_NORMAL)
            sensorMgr!!.registerListener(this, sensorMag, SensorManager.SENSOR_DELAY_NORMAL)
            locationMgr = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            locationMgr!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME.toLong(),
                    MIN_DISTANCE.toFloat(), this)
            try {
                try {
                    val gps = locationMgr!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    val network = locationMgr!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                    when {
                        gps != null -> onLocationChanged(gps)
                        network != null -> onLocationChanged(network)
                        else -> onLocationChanged(ARDataRepository.hardFix)
                    }
                } catch (ex2: Exception) { onLocationChanged(ARDataRepository.hardFix) }
                gmf = GeomagneticField(ARDataRepository.getCurrentLocation().latitude.toFloat(),
                        ARDataRepository.getCurrentLocation().longitude.toFloat(),
                        ARDataRepository.getCurrentLocation().altitude.toFloat(),
                        System.currentTimeMillis())
                angleY = Math.toRadians((-gmf!!.declination).toDouble())
                synchronized(magneticNorthCompensation) {
                    magneticNorthCompensation.toIdentity()
                    magneticNorthCompensation.set(Math.cos(angleY).toFloat(), 0f,
                            Math.sin(angleY).toFloat(), 0f, 1f, 0f,
                            (-Math.sin(angleY)).toFloat(), 0f, Math.cos(angleY).toFloat())
                    magneticNorthCompensation.prod(xAxisRotation)
                }
            } catch (ex: Exception) { ex.printStackTrace() }
        } catch (ex1: Exception) {
            try {
                if (sensorMgr != null) {
                    sensorMgr!!.unregisterListener(this, sensorGrav)
                    sensorMgr!!.unregisterListener(this, sensorMag)
                    sensorMgr = null
                }
                if (locationMgr != null) {
                    locationMgr!!.removeUpdates(this)
                    locationMgr = null
                }
            } catch (ex2: Exception) { ex2.printStackTrace() }
        }
    }

    override fun onStop() {
        super.onStop()
        try { try {
                sensorMgr!!.unregisterListener(this, sensorGrav)
                sensorMgr!!.unregisterListener(this, sensorMag)
            } catch (ex: Exception) { ex.printStackTrace() }
            sensorMgr = null
            try {
                locationMgr!!.removeUpdates(this)
            } catch (ex: Exception) { ex.printStackTrace() }
            locationMgr = null
        } catch (ex: Exception) { ex.printStackTrace() }

    }

    override fun onSensorChanged(evt: SensorEvent) {
        if (!computing.compareAndSet(false, true)) return
        if (evt.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            smooth = LowPassFilter.filter(0.5f, 1.0f, evt.values, gravity)
            gravity[0] = smooth[0]
            gravity[1] = smooth[1]
            gravity[2] = smooth[2]
        } else if (evt.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            smooth = LowPassFilter.filter(2.0f, 4.0f, evt.values, mag)
            mag[0] = smooth[0]
            mag[1] = smooth[1]
            mag[2] = smooth[2]
        }
        SensorManager.getRotationMatrix(temp, null, gravity, mag)
        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, rotation)
        worldCoords.set(rotation[0], rotation[1], rotation[2], rotation[3], rotation[4],
                rotation[5], rotation[6], rotation[7], rotation[8])
        magneticCompensatedCoord.toIdentity()
        synchronized(magneticNorthCompensation) { magneticCompensatedCoord.prod(magneticNorthCompensation) }
        magneticCompensatedCoord.prod(worldCoords)
        magneticCompensatedCoord.invert()
        ARDataRepository.setRotationMatrix(magneticCompensatedCoord)
        computing.set(false)
    }

    //Current stubs
    override fun onProviderDisabled(provider: String) {}
    override fun onProviderEnabled(provider: String) {}
    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}

    override fun onLocationChanged(location: Location) {
        ARDataRepository.setCurrentLocation(location)
        gmf = GeomagneticField(ARDataRepository.getCurrentLocation().latitude.toFloat(),
                ARDataRepository.getCurrentLocation().longitude.toFloat(),
                ARDataRepository.getCurrentLocation().altitude.toFloat(),
                System.currentTimeMillis())
        val angleY = Math.toRadians((-gmf!!.declination).toDouble())
        synchronized(magneticNorthCompensation) {
            magneticNorthCompensation.toIdentity()
            magneticNorthCompensation.set(Math.cos(angleY).toFloat(),
                    0f, Math.sin(angleY).toFloat(), 0f, 1f, 0f,
                    (-Math.sin(angleY)).toFloat(), 0f, Math.cos(angleY).toFloat())
            magneticNorthCompensation.prod(xAxisRotation)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        if (sensor == null) throw NullPointerException()
        if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD
                && accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            Log.e(TAG, "Compass data unreliable")
        }
    }
}