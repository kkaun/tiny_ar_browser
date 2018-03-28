package com.kkaun.mediator.ui.aug.framework.paintables

import android.graphics.Canvas

class GpsSymbol(radius: Float, strokeWidth: Float, fill: Boolean, color: Int) : CommonPaintable() {

    private var radius = 0f
    private var mStrokeWidth = 0f
    private var mFill = false
    private var mColor = 0

    init { set(radius, strokeWidth, fill, color) }

    operator fun set(radius: Float, strokeWidth: Float, fill: Boolean, color: Int) {
        this.radius = radius
        this.mStrokeWidth = strokeWidth
        this.mFill = fill
        this.mColor = color
    }

    override fun paint(canvas: Canvas) {
        setStrokeWidth(mStrokeWidth)
        setFill(mFill)
        setColor(mColor)
        paintCircle(canvas, 0f, 0f, radius)
    }

    override fun getWidth(): Float = radius * 2
    override fun getHeight(): Float = radius * 2
}