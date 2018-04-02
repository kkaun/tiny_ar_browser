package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas
import android.graphics.Color

class Box @JvmOverloads constructor(width: Float, height: Float,
                                    borderColor: Int = Color.rgb(255, 255, 255),
                                    bgColor: Int = Color.argb(128, 0, 0, 0))
    : CommonPaintable() {

    private var width = 0f
    private var height = 0f
    private var borderColor = Color.rgb(255, 255, 255)
    private var backgroundColor = Color.argb(128, 0, 0, 0)

    init { set(width, height, borderColor, bgColor) }

    @JvmOverloads
    fun set(width: Float, height: Float, brdrColor: Int= borderColor, bgColor: Int = backgroundColor) {
        this.width = width
        this.height = height
        this.borderColor = brdrColor
        this.backgroundColor = bgColor
    }

    override fun paint(canvas: Canvas) {
        setFill(true)
        setColor(backgroundColor)
        paintRect(canvas, 0f, 0f, width, height)
        setFill(false)
        setColor(borderColor)
        paintRect(canvas, 0f, 0f, width, height)
    }

    override fun getWidth(): Float = width
    override fun getHeight(): Float = height
}