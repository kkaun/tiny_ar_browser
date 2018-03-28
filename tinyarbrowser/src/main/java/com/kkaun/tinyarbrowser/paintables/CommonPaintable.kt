package com.kkaun.mediator.ui.aug.framework.paintables

import android.graphics.*

abstract class CommonPaintable {

    private var paint: Paint? = Paint(Paint.ANTI_ALIAS_FLAG)

    abstract fun getWidth(): Float
    abstract fun getHeight(): Float

    val textAsc: Float
        get() = -paint!!.ascent()

    val textDesc: Float
        get() = paint!!.descent()

    init {
        if (paint == null) {
            paint = Paint()
            paint!!.textSize = 16f
            paint!!.isAntiAlias = true
            paint!!.color = Color.BLUE
            paint!!.style = Paint.Style.STROKE
        }
    }

    abstract fun paint(canvas: Canvas)

    fun setFill(fill: Boolean) {
        if (fill) paint!!.style = Paint.Style.FILL
        else paint!!.style = Paint.Style.STROKE
    }

    fun setColor(c: Int) {
        paint!!.color = c
    }

    fun setStrokeWidth(w: Float) {
        paint!!.strokeWidth = w
    }

    fun getTextWidth(txt: String?): Float {
        if (txt == null) throw NullPointerException()
        return paint!!.measureText(txt)
    }

    fun setFontSize(size: Float) {
        paint!!.textSize = size
    }

    fun paintLine(canvas: Canvas?, x1: Float, y1: Float, x2: Float, y2: Float) {
        if (canvas == null) throw NullPointerException()

        canvas.drawLine(x1, y1, x2, y2, paint!!)
    }

    fun paintRect(canvas: Canvas?, x: Float, y: Float, width: Float, height: Float) {
        if (canvas == null) throw NullPointerException()

        canvas.drawRect(x, y, x + width, y + height, paint!!)
    }

    fun paintRoundedRect(canvas: Canvas?, x: Float, y: Float, width: Float, height: Float) {
        if (canvas == null) throw NullPointerException()

        val rect = RectF(x, y, x + width, y + height)
        canvas.drawRoundRect(rect, 15f, 15f, paint!!)
    }

    fun paintBitmap(canvas: Canvas?, bitmap: Bitmap?, src: Rect, dst: Rect) {
        if (canvas == null || bitmap == null) throw NullPointerException()

        canvas.drawBitmap(bitmap, src, dst, paint)
    }

    fun paintBitmap(canvas: Canvas?, bitmap: Bitmap?, left: Float, top: Float) {
        if (canvas == null || bitmap == null) throw NullPointerException()

        canvas.drawBitmap(bitmap, left, top, paint)
    }

    fun paintCircle(canvas: Canvas?, x: Float, y: Float, radius: Float) {
        if (canvas == null) throw NullPointerException()

        canvas.drawCircle(x, y, radius, paint!!)
    }

    fun paintText(canvas: Canvas?, x: Float, y: Float, text: String?) {
        if (canvas == null || text == null) throw NullPointerException()

        canvas.drawText(text, x, y, paint!!)
    }

    fun paintObj(canvas: Canvas?, obj: CommonPaintable?,
                 x: Float, y: Float,
                 rotation: Float, scale: Float) {
        if (canvas == null || obj == null) throw NullPointerException()

        canvas.save()
        canvas.translate(x + obj.getWidth() / 2, y + obj.getHeight() / 2)
        canvas.rotate(rotation)
        canvas.scale(scale, scale)
        canvas.translate(-(obj.getWidth() / 2), -(obj.getHeight() / 2))
        obj.paint(canvas)
        canvas.restore()
    }

    fun paintPath(canvas: Canvas?, path: Path?,
                  x: Float, y: Float, width: Float,
                  height: Float, rotation: Float, scale: Float) {
        if (canvas == null || path == null) throw NullPointerException()
        canvas.save()
        canvas.translate(x + width / 2, y + height / 2)
        canvas.rotate(rotation)
        canvas.scale(scale, scale)
        canvas.translate(-(width / 2), -(height / 2))
        canvas.drawPath(path, paint!!)
        canvas.restore()
    }
}