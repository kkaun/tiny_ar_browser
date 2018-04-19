package com.kkaun.tinyarbrowser.samples.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.content.ContextCompat

/**
 * Created by Кира on 03.04.2018.
 */


object ColdLocationRequestHelper {

    interface LocationCallback {
        fun onNewLocationAvailable(location: GPSCoordinates)
        fun onLocationFailure()
        fun onProviderDisabled()
    }

    interface ColdLocationReceiver {
        fun onColdLocationReceived(location: Location)
        fun onColdLocationFailure()
        fun onProviderDisabled()
    }

    fun requestColdLocationUpdate(ctx: Context, receiver: ColdLocationReceiver) {
        requestSingleUpdate(ctx, object : LocationCallback {
            override fun onNewLocationAvailable(location: GPSCoordinates) {
                val mLocation = Location(LocationManager.GPS_PROVIDER)
                mLocation.latitude = location.latitude
                mLocation.longitude = location.longitude
                receiver.onColdLocationReceived(mLocation)
            }

            override fun onLocationFailure() {
                receiver.onColdLocationFailure()
            }

            override fun onProviderDisabled() {
                receiver.onProviderDisabled()
            }
        })
    }

    private fun requestSingleUpdate(context: Context, callback: LocationCallback) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        if (isNetworkEnabled) {
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_COARSE
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context,
                            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // Show settings alert
            } else {
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback.onNewLocationAvailable(GPSCoordinates(location.latitude, location.longitude))
                    }
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                        when (status) {
                            LocationProvider.OUT_OF_SERVICE -> callback.onLocationFailure()
                            LocationProvider.TEMPORARILY_UNAVAILABLE -> callback.onLocationFailure()
                        }
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) { callback.onProviderDisabled() }
                }, null)
            }
        } else {
            val isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            if (isGPSEnabled) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_FINE
                locationManager.requestSingleUpdate(criteria, object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        callback.onNewLocationAvailable(GPSCoordinates(location.latitude, location.longitude))
                    }
                    override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                        when (status) {
                            LocationProvider.OUT_OF_SERVICE -> callback.onLocationFailure()
                            LocationProvider.TEMPORARILY_UNAVAILABLE -> callback.onLocationFailure()
                        }
                    }
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) { callback.onProviderDisabled() }
                }, null)
            }
        }
    }

    class GPSCoordinates(theLatitude: Double, theLongitude: Double) {
        var longitude = (-1).toDouble()
        var latitude = (-1).toDouble()
        init {
            longitude = theLongitude
            latitude = theLatitude
        }
    }
}