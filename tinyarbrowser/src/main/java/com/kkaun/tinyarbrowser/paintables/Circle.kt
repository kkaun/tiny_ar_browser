package com.kkaun.mediator.ui.aug.framework.paintables

import android.graphics.Canvas

class Circle(color: Int, radius: Float, fill: Boolean) : CommonPaintable() {

    private var mColor = 0
    private var radius = 0f
    private var mFill = false

    init { set(color, radius, fill) }

    operator fun set(color: Int, radius: Float, fill: Boolean) {
        this.mColor = color
        this.radius = radius
        this.mFill = fill
    }

    override fun paint(canvas: Canvas) {
        setFill(mFill)
        setColor(mColor)
        paintCircle(canvas, 0f, 0f, radius)
    }

    override fun getWidth(): Float = radius * 2
    override fun getHeight(): Float = radius * 2
}