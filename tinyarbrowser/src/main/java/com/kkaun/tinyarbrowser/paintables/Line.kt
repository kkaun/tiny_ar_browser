package com.kkaun.mediator.ui.aug.framework.paintables

import android.graphics.Canvas

class Line(color: Int, x: Float, y: Float) : CommonPaintable() {

    private var mColor = 0
    private var x = 0f
    private var y = 0f

    init { set(color, x, y) }

    operator fun set(color: Int, x: Float, y: Float) {
        this.mColor = color
        this.x = x
        this.y = y
    }

    override fun paint(canvas: Canvas) {
        setFill(false)
        setColor(mColor)
        paintLine(canvas, 0f, 0f, x, y)
    }

    override fun getWidth(): Float = x
    override fun getHeight(): Float = y
}