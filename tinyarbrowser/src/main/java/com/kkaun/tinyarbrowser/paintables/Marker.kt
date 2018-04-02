package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.util.Log
import com.kkaun.tinyarbrowser.camera.CameraModel
import com.kkaun.tinyarbrowser.data.ARDataRepository
import com.kkaun.tinyarbrowser.projection.Vector
import com.kkaun.tinyarbrowser.util.PhysicalLocationHelper
import com.kkaun.tinyarbrowser.util.getAngle
import java.text.DecimalFormat
import java.util.*

open class Marker(name: String, latitude: Double, longitude: Double,
                  altitude: Double, color: Int) : Comparable<Marker> {

    companion object {
        private val DECIMAL_FORMAT = DecimalFormat("@#")
        private val symbolVector = Vector(0f, 0f, 0f)
        private val textVector = Vector(0f, 1f, 0f)
        private val debugTouchZone = false
        private var touchBox: Box? = null
        private var touchPosition: Position? = null
        private val debugCollisionZone = false
        private var collisionBox: Box? = null
        private var collisionPosition: Position? = null
        @Volatile
        private var cam: CameraModel? = null
    }

    private val screenPositionVector = Vector()
    private val tmpSymbolVector = Vector()
    private val tmpVector = Vector()
    private val tmpTextVector = Vector()
    private val distanceArray = FloatArray(1)
    private val locationArray = FloatArray(3)
    private val screenPositionArray = FloatArray(3)
    protected val symbolArray = FloatArray(3)
    protected val textArray = FloatArray(3)

    @get:Synchronized
    var initialY = 0.0f
        private set
    @Volatile
    private var textBox: BoxedText? = null
    @Volatile
    private var textContainer: Position? = null
    @Volatile
    protected var gpsSymbol: CommonPaintable? = null
    @Volatile
    protected var symbolContainer: Position? = null
    @get:Synchronized
    var name: String? = null
        protected set
    @Volatile
    protected var physicalLocationHelper = PhysicalLocationHelper()
    @Volatile
    @get:Synchronized
    var distance = 0.0
        protected set
    @Volatile
    @get:Synchronized
    var isOnRadar = false
        protected set
    @Volatile
    @get:Synchronized
    var isInView = false
        protected set
    protected val symbolXyzRelativeToCameraView = Vector()
    protected val textXyzRelativeToCameraView = Vector()
    @get:Synchronized
    val location = Vector()
    @get:Synchronized
    var color = Color.WHITE
        protected set

    val screenPosition: Vector
        @Synchronized get() {
            symbolXyzRelativeToCameraView[symbolArray]
            textXyzRelativeToCameraView[textArray]
            val x = (symbolArray[0] + textArray[0]) / 2
            var y = (symbolArray[1] + textArray[1]) / 2
            val z = (symbolArray[2] + textArray[2]) / 2
            if (textBox != null) y += textBox!!.getWidth() / 2
            screenPositionVector[x, y] = z
            return screenPositionVector
        }

    val height: Float
        @Synchronized get() = if (symbolContainer == null || textContainer == null) 0f
        else symbolContainer!!.getHeight() + textContainer!!.getHeight()

    val width: Float
        @Synchronized get() {
            if (symbolContainer == null || textContainer == null) return 0f
            val w1 = textContainer!!.getWidth()
            val w2 = symbolContainer!!.getWidth()
            return if (w1 > w2) w1 else w2
        }

    init { set(name, latitude, longitude, altitude, color) }

    @Synchronized
    operator fun set(name: String?, latitude: Double, longitude: Double, altitude: Double, color: Int) {
        if (name == null) throw NullPointerException()
        this.name = name
        this.physicalLocationHelper[latitude, longitude] = altitude
        this.color = color
        this.isOnRadar = false
        this.isInView = false
        this.symbolXyzRelativeToCameraView[0f, 0f] = 0f
        this.textXyzRelativeToCameraView[0f, 0f] = 0f
        this.location[0f, 0f] = 0f
        this.initialY = 0.0f
    }

    @Synchronized
    fun update(canvas: Canvas?, addX: Float, addY: Float) {
        if (canvas == null) throw NullPointerException()
        if (cam == null) cam = CameraModel(canvas.width, canvas.height, true)
        cam!!.set(canvas.width, canvas.height, false)
        cam!!.setViewAngle(CameraModel.DEFAULT_VIEW_ANGLE)
        populateMatrices(cam, addX, addY)
        updateRadar()
        updateView()
    }

    @Synchronized
    private fun populateMatrices(cam: CameraModel?, addX: Float, addY: Float) {
        if (cam == null) throw NullPointerException()
        tmpSymbolVector.set(symbolVector)
        tmpSymbolVector.add(location)
        tmpSymbolVector.prod(ARDataRepository.getRotationMatrix())
        tmpTextVector.set(textVector)
        tmpTextVector.add(location)
        tmpTextVector.prod(ARDataRepository.getRotationMatrix())
        cam.projectPoint(tmpSymbolVector, tmpVector, addX, addY)
        symbolXyzRelativeToCameraView.set(tmpVector)
        cam.projectPoint(tmpTextVector, tmpVector, addX, addY)
        textXyzRelativeToCameraView.set(tmpVector)
    }

    @Synchronized
    private fun updateRadar() {
        isOnRadar = false
        val range = ARDataRepository.getRadius() * 1000
        val scale = range / Radar.mRadarBodyRadius
        location[locationArray]
        val x = locationArray[0] / scale
        val y = locationArray[2] / scale // z==y Switched on purpose
        symbolXyzRelativeToCameraView[symbolArray]
        if (symbolArray[2] < -1f && x * x + y * y < Radar.mRadarBodyRadius * Radar.mRadarBodyRadius) {
            isOnRadar = true
        }
    }

    @Synchronized
    private fun updateView() {
        isInView = false
        symbolXyzRelativeToCameraView[symbolArray]
        val x1 = symbolArray[0] + width / 2
        val y1 = symbolArray[1] + height / 2
        val x2 = symbolArray[0] - width / 2
        val y2 = symbolArray[1] - height / 2
        if (x1 >= -1 && x2 <= cam!!.width && y1 >= -1 && y2 <= cam!!.height) isInView = true
    }

    @Synchronized
    fun calcRelativePosition(location: Location?) {
        if (location == null) throw NullPointerException()
        updateDistance(location)
        if (physicalLocationHelper.altitude == 0.0) physicalLocationHelper.altitude = location.altitude
        PhysicalLocationHelper.convertLocationToVector(location, physicalLocationHelper, this.location)
        this.initialY = this.location.y
        updateRadar()
    }

    @Synchronized
    private fun updateDistance(location: Location?) {
        if (location == null) throw NullPointerException()
        Location.distanceBetween(physicalLocationHelper.latitude, physicalLocationHelper.longitude,
                location.latitude, location.longitude, distanceArray)
        distance = distanceArray[0].toDouble()
    }

    @Synchronized
    fun handleClick(x: Float, y: Float): Boolean {
        return if (!isOnRadar || !isInView) false else isPointOnMarker(x, y, this)
    }

    @Synchronized
    fun isMarkerOnMarker(marker: Marker): Boolean {
        return isMarkerOnMarker(marker, true)
    }

    @Synchronized
    private fun isMarkerOnMarker(marker: Marker, reflect: Boolean): Boolean {
        marker.screenPosition[screenPositionArray]
        val x = screenPositionArray[0]
        val y = screenPositionArray[1]
        val middleOfMarker = isPointOnMarker(x, y, this)
        if (middleOfMarker) return true
        val halfWidth = marker.width / 2
        val halfHeight = marker.height / 2
        val x1 = x - halfWidth
        val y1 = y - halfHeight
        val upperLeftOfMarker = isPointOnMarker(x1, y1, this)
        if (upperLeftOfMarker) return true
        val x2 = x + halfWidth
        val upperRightOfMarker = isPointOnMarker(x2, y1, this)
        if (upperRightOfMarker) return true
        val y3 = y + halfHeight
        val lowerLeftOfMarker = isPointOnMarker(x1, y3, this)
        if (lowerLeftOfMarker) return true
        val lowerRightOfMarker = isPointOnMarker(x2, y3, this)
        if (lowerRightOfMarker) return true
        return if (reflect) marker.isMarkerOnMarker(this, false) else false
    }

    @Synchronized
    private fun isPointOnMarker(x: Float, y: Float, marker: Marker): Boolean {
        marker.screenPosition[screenPositionArray]
        val myX = screenPositionArray[0]
        val myY = screenPositionArray[1]
        val adjWidth = marker.width / 2
        val adjHeight = marker.height / 2
        val x1 = myX - adjWidth
        val y1 = myY - adjHeight
        val x2 = myX + adjWidth
        val y2 = myY + adjHeight
        return x in x1..x2 && y >= y1 && y <= y2
    }

    @Synchronized
    fun draw(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        if (!isOnRadar || !isInView) return
        if (debugTouchZone) drawTouchZone(canvas)
        if (debugCollisionZone) drawCollisionZone(canvas)
        drawIcon(canvas)
        drawText(canvas)
    }

    @Synchronized
    protected fun drawCollisionZone(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        screenPosition[screenPositionArray]
        val x = screenPositionArray[0]
        val y = screenPositionArray[1]
        val width = width
        val height = height
        val halfWidth = width / 2
        val halfHeight = height / 2
        val x1 = x - halfWidth
        val y1 = y - halfHeight
        val x2 = x + halfWidth
        val y3 = y + halfHeight

        Log.w("collisionBox", "ul (x=$x1 y=$y1)")
        Log.w("collisionBox", "ur (x=$x2 y=$y1)")
        Log.w("collisionBox", "ll (x=$x1 y=$y3)")
        Log.w("collisionBox", "lr (x=$x2 y=$y3)")

        if (collisionBox == null) collisionBox = Box(width, height, Color.WHITE, Color.RED)
        else collisionBox!!.set(width, height)
        val currentAngle = getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]) + 90
        if (collisionPosition == null) collisionPosition = Position(collisionBox!!,
                x1, y1, currentAngle, 1f)
        else collisionPosition!![collisionBox, x1, y1, currentAngle] = 1f
        collisionPosition!!.paint(canvas)
    }

    @Synchronized
    protected fun drawTouchZone(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        if (gpsSymbol == null) return
        symbolXyzRelativeToCameraView[symbolArray]
        textXyzRelativeToCameraView[textArray]
        val x1 = symbolArray[0]
        val y1 = symbolArray[1]
        val x2 = textArray[0]
        val y2 = textArray[1]
        val width = width
        val height = height
        var adjX = (x1 + x2) / 2
        var adjY = (y1 + y2) / 2
        val currentAngle = getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1]) + 90
        adjX -= width / 2
        adjY -= gpsSymbol!!.getHeight() / 2
        Log.w("touchBox", "ul (x=$adjX y=$adjY)")
        Log.w("touchBox", "ur (x=" + (adjX + width) + " y=" + adjY + ")")
        Log.w("touchBox", "ll (x=" + adjX + " y=" + (adjY + height) + ")")
        Log.w("touchBox", "lr (x=" + (adjX + width) + " y=" + (adjY + height) + ")")
        if (touchBox == null) touchBox = Box(width, height, Color.WHITE, Color.GREEN)
        else touchBox!!.set(width, height)
        if (touchPosition == null) touchPosition = Position(touchBox!!,
                adjX, adjY, currentAngle, 1f)
        else touchPosition!![touchBox, adjX, adjY, currentAngle] = 1f
        touchPosition!!.paint(canvas)
    }

    @Synchronized
    protected open fun drawIcon(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        if (gpsSymbol == null) gpsSymbol = GpsSymbol(36f, 36f, true, color)
        textXyzRelativeToCameraView[textArray]
        symbolXyzRelativeToCameraView[symbolArray]
        val currentAngle = getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1])
        val angle = currentAngle + 90
        if (symbolContainer == null) symbolContainer = Position(gpsSymbol!!,
                symbolArray[0], symbolArray[1], angle, 1f)
        else symbolContainer!![gpsSymbol, symbolArray[0], symbolArray[1], angle] = 1f
        symbolContainer!!.paint(canvas)
    }

    @Synchronized
    protected fun drawText(canvas: Canvas?) {
        if (canvas == null) throw NullPointerException()
        var textStr: String? = null
        textStr = if (distance < 1000.0) name + " (" + DECIMAL_FORMAT.format(distance) + "m)"
        else {
            val d = distance / 1000.0
            name + " (" + DECIMAL_FORMAT.format(d) + "km)"
        }
        textXyzRelativeToCameraView[textArray]
        symbolXyzRelativeToCameraView[symbolArray]
        val maxHeight = (Math.round(canvas.height / 10f) + 1).toFloat()
        if (textBox == null) textBox = BoxedText(textStr,
                (Math.round(maxHeight / 2f) + 1).toFloat(), 300f)
        else textBox!!.set(textStr, (Math.round(maxHeight / 2f) + 1).toFloat(), 300f)
        val currentAngle = getAngle(symbolArray[0], symbolArray[1], textArray[0], textArray[1])
        val angle = currentAngle + 90
        val x = textArray[0] - textBox!!.getWidth() / 2
        val y = textArray[1] + maxHeight
        if (textContainer == null) textContainer = Position(textBox!!, x, y, angle, 1f)
        else textContainer!![textBox, x, y, angle] = 1f
        textContainer!!.paint(canvas)
    }

    @Synchronized
    override fun compareTo(other: Marker): Int {
        return name!!.compareTo(other.name!!)
    }

    @Synchronized
    override fun equals(other: Any?): Boolean {
        if (other == null || name == null) throw NullPointerException()
        return name == (other as Marker).name
    }

    override fun hashCode(): Int {
        var result = screenPositionVector.hashCode()
        result = 31 * result + tmpSymbolVector.hashCode()
        result = 31 * result + tmpVector.hashCode()
        result = 31 * result + tmpTextVector.hashCode()
        result = 31 * result + Arrays.hashCode(distanceArray)
        result = 31 * result + Arrays.hashCode(locationArray)
        result = 31 * result + Arrays.hashCode(screenPositionArray)
        result = 31 * result + Arrays.hashCode(symbolArray)
        result = 31 * result + Arrays.hashCode(textArray)
        result = 31 * result + initialY.hashCode()
        result = 31 * result + (textBox?.hashCode() ?: 0)
        result = 31 * result + (textContainer?.hashCode() ?: 0)
        result = 31 * result + (gpsSymbol?.hashCode() ?: 0)
        result = 31 * result + (symbolContainer?.hashCode() ?: 0)
        result = 31 * result + (name?.hashCode() ?: 0)
        result = 31 * result + physicalLocationHelper.hashCode()
        result = 31 * result + distance.hashCode()
        result = 31 * result + isOnRadar.hashCode()
        result = 31 * result + isInView.hashCode()
        result = 31 * result + symbolXyzRelativeToCameraView.hashCode()
        result = 31 * result + textXyzRelativeToCameraView.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + color
        return result
    }
}