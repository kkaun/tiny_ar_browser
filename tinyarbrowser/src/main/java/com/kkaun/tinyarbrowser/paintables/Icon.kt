package com.kkaun.tinyarbrowser.paintables

import android.graphics.Bitmap
import android.graphics.Canvas

class Icon(bitmap: Bitmap, width: Int, height: Int) : CommonPaintable() {

    private var bitmap: Bitmap? = null

    init { set(bitmap, width, height) }

    operator fun set(bitmap: Bitmap?, width: Int, height: Int) {
        if (bitmap == null) throw NullPointerException()
        this.bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
    }

    override fun paint(canvas: Canvas) {
        if (bitmap == null) throw NullPointerException()
        paintBitmap(canvas, bitmap, (-(bitmap!!.width / 2)).toFloat(), (-(bitmap!!.height / 2)).toFloat())
    }

    override fun getWidth(): Float = bitmap!!.width.toFloat()
    override fun getHeight(): Float = bitmap!!.height.toFloat()
}