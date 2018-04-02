package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas

class Position(drawObj: CommonPaintable, x: Float, y: Float, rotation: Float, scale: Float) : CommonPaintable() {

    private var obj: CommonPaintable? = null
    private var width = 0f
    private var height = 0f
    private var objRotation = 0f
    private var objScale = 0f
    var objectsX = 0f
        private set
    var objectsY = 0f
        private set

    init { set(drawObj, x, y, rotation, scale) }

    operator fun set(drawObj: CommonPaintable?, x: Float, y: Float, rotation: Float, scale: Float) {
        if (drawObj == null) throw NullPointerException()
        this.obj = drawObj
        this.objectsX = x
        this.objectsY = y
        this.objRotation = rotation
        this.objScale = scale
        this.width = obj!!.getWidth()
        this.height = obj!!.getHeight()
    }

    fun move(x: Float, y: Float) {
        objectsX = x
        objectsY = y
    }

    override fun paint(canvas: Canvas) {
        if (obj == null) throw NullPointerException()
        paintObj(canvas, obj, objectsX, objectsY, objRotation, objScale)
    }

    override fun getWidth() = width
    override fun getHeight() = height

    override fun toString(): String {
        return "objX=$objectsX objY=$objectsY width=$width height=$height"
    }
}