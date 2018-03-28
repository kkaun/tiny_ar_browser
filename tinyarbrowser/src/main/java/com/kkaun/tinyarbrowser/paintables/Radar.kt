package com.kkaun.mediator.ui.aug.framework.paintables

import android.graphics.Canvas
import android.graphics.Color
import com.kkaun.mediator.ui.aug.framework.camera.CameraModel
import com.kkaun.mediator.ui.aug.framework.data.ARDataRepository
import com.kkaun.mediator.ui.aug.framework.projection.PitchAzimuthCalculator
import com.kkaun.mediator.ui.aug.framework.util.ScreenPositionHelper
import com.kkaun.mediator.ui.aug.framework.util.calcColorRange

class Radar {

    companion object {
        private val PAD_X = 10f
        private val PAD_Y = 20f
        private val TEXT_SIZE = 12
        private var leftRadarLine: ScreenPositionHelper? = null
        private var rightRadarLine: ScreenPositionHelper? = null
        private var leftLineContainer: Position? = null
        private var rightLineContainer: Position? = null
        private var circleContainer: Position? = null
        private var radarPoints: RadarPoints? = null
        private var pointsContainer: Position? = null
        private var text: Text? = null
        private var paintedContainer: Position? = null
        var mRadarBodyRadius = 50f //48f
        var mRadarBodyColor = Color.argb(100, 0, 0, 200)
        var mTextColor = Color.rgb(255, 255, 255)
        var mLineColor = Color.argb(150, 0, 0, 220)
    }

    init {
        if (leftRadarLine == null) leftRadarLine = ScreenPositionHelper()
        if (rightRadarLine == null) rightRadarLine = ScreenPositionHelper()
    }

    fun setRadarBodyRadius(radius: Float) {
        mRadarBodyRadius = if(radius < 50f) 50f else if (radius > 200f) 200f else radius
    }
    fun setRadarBodyColor(alpha: Int, red: Int, green: Int, blue: Int) {
        mRadarBodyColor = Color.argb(calcColorRange(alpha), calcColorRange(red),
                calcColorRange(green), calcColorRange(blue))
    }
    fun setTextColor(red: Int, green: Int, blue: Int) {
        mTextColor = Color.rgb(calcColorRange(red), calcColorRange(green), calcColorRange(blue))
    }
    fun setLineColor(red: Int, green: Int, blue: Int) {
        mLineColor = Color.rgb(calcColorRange(red), calcColorRange(green), calcColorRange(blue))
    }

    fun getRadarBoduRadius() = mRadarBodyRadius
    fun getRadarBodyColor() = mRadarBodyColor
    fun getTextColor() = mTextColor
    fun getLineColor() = mLineColor


    fun draw(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        PitchAzimuthCalculator.calcPitchBearing(ARDataRepository.getRotationMatrix())
        ARDataRepository.setAzimuth(PitchAzimuthCalculator.azimuth)
        ARDataRepository.setPitch(PitchAzimuthCalculator.pitch)
        drawRadarCircle(canvas)
        drawRadarPoints(canvas)
        drawRadarLines(canvas)
        drawRadarText(canvas)
    }

    private fun drawRadarCircle(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        if (circleContainer == null) {
            val paintableCircle = Circle(mRadarBodyColor, mRadarBodyRadius, true)
            circleContainer = Position(paintableCircle, PAD_X + mRadarBodyRadius, PAD_Y + mRadarBodyRadius, 0f, 1f)
        }
        circleContainer!!.paint(canvas)
    }

    private fun drawRadarPoints(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        if (radarPoints == null) radarPoints = RadarPoints()
        if (pointsContainer == null) pointsContainer = Position(
                radarPoints!!, PAD_X, PAD_Y, -ARDataRepository.getAzimuth(), 1f)
        else pointsContainer!!.set(radarPoints, PAD_X, PAD_Y, -ARDataRepository.getAzimuth(), 1f)
        pointsContainer!!.paint(canvas)
    }

    private fun drawRadarLines(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        if (leftLineContainer == null) {
            leftRadarLine!!.set(0f, -mRadarBodyRadius)
            leftRadarLine!!.rotate((-CameraModel.DEFAULT_VIEW_ANGLE / 2).toDouble())
            leftRadarLine!!.add(PAD_X + mRadarBodyRadius, PAD_Y + mRadarBodyRadius)
            val leftX = leftRadarLine!!.x - (PAD_X + mRadarBodyRadius)
            val leftY = leftRadarLine!!.y - (PAD_Y + mRadarBodyRadius)
            val leftLine = Line(mLineColor, leftX, leftY)
            leftLineContainer = Position(leftLine, PAD_X + mRadarBodyRadius,
                    PAD_Y + mRadarBodyRadius, 0f, 1f)
        }
        leftLineContainer!!.paint(canvas)
        if (rightLineContainer == null) {
            rightRadarLine!!.set(0f, -mRadarBodyRadius)
            rightRadarLine!!.rotate((CameraModel.DEFAULT_VIEW_ANGLE / 2).toDouble())
            rightRadarLine!!.add(PAD_X + mRadarBodyRadius, PAD_Y + mRadarBodyRadius)
            val rightX = rightRadarLine!!.x - (PAD_X + mRadarBodyRadius)
            val rightY = rightRadarLine!!.y - (PAD_Y + mRadarBodyRadius)
            val rightLine = Line(mLineColor, rightX, rightY)
            rightLineContainer = Position(rightLine,
                    PAD_X + mRadarBodyRadius,
                    PAD_Y + mRadarBodyRadius,
                    0f,
                    1f)
        }
        rightLineContainer!!.paint(canvas)
    }

    private fun drawRadarText(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        val range = (ARDataRepository.getAzimuth() / (360f / 16f)).toInt()
        var dirTxt = ""
        if (range == 15 || range == 0) dirTxt = "N"
        else if (range == 1 || range == 2) dirTxt = "NE"
        else if (range == 3 || range == 4) dirTxt = "E"
        else if (range == 5 || range == 6) dirTxt = "SE"
        else if (range == 7 || range == 8) dirTxt = "S"
        else if (range == 9 || range == 10) dirTxt = "SW"
        else if (range == 11 || range == 12) dirTxt = "W"
        else if (range == 13 || range == 14) dirTxt = "NW"
        val bearing = ARDataRepository.getAzimuth().toInt()
        radarText(canvas, "" + bearing + 176.toChar() + " " + dirTxt,
                PAD_X + mRadarBodyRadius, PAD_Y - 5, true)
        radarText(canvas, formatDist(ARDataRepository.getRadius() * 1000),
                PAD_X + mRadarBodyRadius, PAD_Y + mRadarBodyRadius * 2 - 10, false
        )
    }

    private fun radarText(canvas: Canvas?, txt: String?, x: Float, y: Float, bg: Boolean) {
        if (canvas == null || txt == null) throw NullPointerException()
        if (text == null)
            text = Text(txt, mTextColor, TEXT_SIZE, bg)
        else text!!.set(txt, mTextColor, TEXT_SIZE, bg)
        if (paintedContainer == null)
            paintedContainer = Position(text!!, x, y, 0f, 1f)
        else paintedContainer!!.set(text, x, y, 0f, 1f)
        paintedContainer!!.paint(canvas)
    }

    private fun formatDist(meters: Float): String {
        return when {
            meters < 1000 -> meters.toInt().toString() + "m"
            meters < 10000 -> formatDec(meters / 1000f, 1) + "km"
            else -> (meters / 1000f).toInt().toString() + "km"
        }
    }

    private fun formatDec(`val`: Float, dec: Int): String {
        val factor = Math.pow(10.0, dec.toDouble()).toInt()
        val front = `val`.toInt()
        val back = Math.abs(`val` * factor).toInt() % factor
        return front.toString() + "." + back
    }
}