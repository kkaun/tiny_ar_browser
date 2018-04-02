package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas

class Point(color: Int, fill: Boolean) : CommonPaintable() {

    companion object {
        private val mWidth = 2
        private val mHeight = 2
    }
    private var mColor = 0
    private var mFill = false

    init { set(color, fill) }

    operator fun set(color: Int, fill: Boolean) {
        this.mColor = color
        this.mFill = fill
    }

    override fun paint(canvas: Canvas) {
        setFill(mFill)
        setColor(mColor)
        paintRect(canvas, -1f, -1f, mWidth.toFloat(), mHeight.toFloat())
    }

    override fun getWidth(): Float = mWidth.toFloat()
    override fun getHeight(): Float = mHeight.toFloat()
}