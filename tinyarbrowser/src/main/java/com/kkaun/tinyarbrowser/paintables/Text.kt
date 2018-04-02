package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas
import android.graphics.Color

class Text(text: String, color: Int, size: Int, paintBackground: Boolean) : CommonPaintable() {

    companion object {
        private val WIDTH_PAD = 4f
        private val HEIGHT_PAD = 2f
    }

    private var text: String? = null
    private var mColor = 0
    private var size = 0
    private var width = 0f
    private var height = 0f
    private var bg = false

    init {
        set(text, color, size, paintBackground)
    }

    operator fun set(text: String?, color: Int, size: Int, paintBackground: Boolean) {
        if (text == null) throw NullPointerException()

        this.text = text
        this.bg = paintBackground
        this.mColor = color
        this.size = size
        this.width = getTextWidth(text) + WIDTH_PAD * 2
        this.height = textAsc + textDesc + HEIGHT_PAD * 2
    }

    override fun paint(canvas: Canvas) {
        setColor(mColor)
        setFontSize(size.toFloat())
        if (bg) {
            setColor(Color.rgb(0, 0, 0))
            setFill(true)
            paintRect(canvas, -(width / 2), -(height / 2), width, height)
            setColor(Color.rgb(255, 255, 255))
            setFill(false)
            paintRect(canvas, -(width / 2), -(height / 2), width, height)
        }
        paintText(canvas, WIDTH_PAD - width / 2, HEIGHT_PAD + textAsc - height / 2, text)
    }

    override fun getWidth(): Float { return width }
    override fun getHeight(): Float { return height }
}