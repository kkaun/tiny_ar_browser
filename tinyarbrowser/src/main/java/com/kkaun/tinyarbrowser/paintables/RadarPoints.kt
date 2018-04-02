package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas
import com.kkaun.tinyarbrowser.data.ARDataRepository

class RadarPoints : CommonPaintable() {
    private val locationArray = FloatArray(3)
    private var point: Point? = null
    private var pointContainer: Position? = null

    override fun paint(canvas: Canvas) {
        val range = ARDataRepository.getRadius() * 1000
        val scale = range / Radar.mRadarBodyRadius
        for (pm in ARDataRepository.markers) {
            pm.location.get(locationArray)
            val x = locationArray[0] / scale
            val y = locationArray[2] / scale
            if (x * x + y * y < Radar.mRadarBodyRadius * Radar.mRadarBodyRadius) {
                if (point == null) point = Point(pm.color, true)
                else point!!.set(pm.color, true)
                if (pointContainer == null) pointContainer = Position(point!!,
                        x + Radar.mRadarBodyRadius - 1, y + Radar.mRadarBodyRadius - 1, 0f, 1f)
                else pointContainer!!.set(point, x + Radar.mRadarBodyRadius - 1,
                        y + Radar.mRadarBodyRadius - 1, 0f, 1f)
                pointContainer!!.paint(canvas)
            }
        }
    }

    override fun getWidth(): Float = Radar.mRadarBodyRadius * 2
    override fun getHeight(): Float = Radar.mRadarBodyRadius * 2
}