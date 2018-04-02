package com.kkaun.tinyarbrowser.paintables

import android.graphics.Bitmap
import android.graphics.Canvas
import com.kkaun.tinyarbrowser.util.getAngle

class ARMarker(name: String, latitude: Double, longitude: Double,
               altitude: Double, color: Int, bitmap: Bitmap)
    : Marker(name, latitude, longitude, altitude, color) {

    //TODO: get rid of BITMAP as class field
    private var bitmap: Bitmap? = null

    init { this.bitmap = bitmap }

    public override fun drawIcon(canvas: Canvas?) {
        if (canvas == null || bitmap == null) throw NullPointerException()
        if (gpsSymbol == null) gpsSymbol = Icon(bitmap!!, 96, 96)
        textXyzRelativeToCameraView.get(textArray)
        symbolXyzRelativeToCameraView.get(symbolArray)
        val currentAngle = getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1])
        val angle = currentAngle + 90
        if (symbolContainer == null) symbolContainer = Position(
                gpsSymbol!!, symbolArray[0], symbolArray[1], angle, 1f)
        else symbolContainer!!.set(gpsSymbol, symbolArray[0], symbolArray[1], angle, 1f)
        symbolContainer!!.paint(canvas)
    }
}