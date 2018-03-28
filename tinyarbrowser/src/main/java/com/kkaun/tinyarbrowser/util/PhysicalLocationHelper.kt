package com.kkaun.mediator.ui.aug.framework.util

import android.location.Location
import com.kkaun.mediator.ui.aug.framework.projection.Vector

class PhysicalLocationHelper() {

    companion object {
        private val x = FloatArray(1)
        private var y = 0.0
        private val z = FloatArray(1)

        @Synchronized
        fun convertLocationToVector(org: Location?, gp: PhysicalLocationHelper?, v: Vector?) {
            if (org == null || gp == null || v == null)
                throw NullPointerException("Location, PhysicalLocationHelper, and Vector cannot be NULL.")
            Location.distanceBetween(org.latitude, org.longitude, gp.latitude, org.longitude, z)
            Location.distanceBetween(org.latitude, org.longitude, org.latitude, gp.longitude, x)
            y = gp.altitude - org.altitude
            if (org.latitude < gp.latitude) z[0] *= -1f
            if (org.longitude > gp.longitude) x[0] *= -1f
            v[x[0], y.toFloat()] = z[0]
        }
    }
    var latitude = 0.0
    var longitude = 0.0
    var altitude = 0.0

    operator fun set(latitude: Double, longitude: Double, altitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
        this.altitude = altitude
    }

    override fun toString(): String {
        return "(lat=$latitude, lng=$longitude, alt=$altitude)"
    }
}