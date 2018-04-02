package com.kkaun.tinyarbrowser.paintables

import android.graphics.Canvas
import android.graphics.Color
import java.text.BreakIterator
import java.util.*

class BoxedText @JvmOverloads constructor(txtInit: String, fontSizeInit: Float, maxWidth: Float, borderColor: Int = Color.rgb(255, 255, 255), bgColor: Int = Color.argb(128, 0, 0, 0), textColor: Int = Color.rgb(255, 255, 255)) : CommonPaintable() {

    private var mWidth = 0f
    private var mHeight = 0f
    private var lineList: ArrayList<String>? = null
    private var lines: Array<String>? = null
    private var lineWidths: FloatArray? = null
    private var lineHeight = 0f
    private var pad = 0f
    private var mFontSize = 12f
    private var borderColor = Color.rgb(255, 255, 255)
    private var backgroundColor = Color.argb(160, 0, 0, 0)
    private var textColor = Color.rgb(255, 255, 255)

    init { set(txtInit, fontSizeInit, maxWidth, borderColor, bgColor, textColor) }

    operator fun set(txtInit: String?, fontSizeInit: Float, maxWidth: Float, borderColor: Int, bgColor: Int, textColor: Int) {
        if (txtInit == null) throw NullPointerException()
        this.borderColor = borderColor
        this.backgroundColor = bgColor
        this.textColor = textColor
        this.pad = textAsc
        set(txtInit, fontSizeInit, maxWidth)
    }

    operator fun set(txtInit: String?, fontSizeInit: Float, maxWidth: Float) {
        if (txtInit == null) throw NullPointerException()
        try {
            prepTxt(txtInit, fontSizeInit, maxWidth)
        } catch (ex: Exception) {
            ex.printStackTrace()
            prepTxt("TEXT PARSE ERROR", 12f, 200f)
        }

    }

    private fun prepTxt(txtInit: String?, fontSizeInit: Float, maxWidth: Float) {
        if (txtInit == null) throw NullPointerException()
        setFontSize(fontSizeInit)
        mFontSize = fontSizeInit
        var areaWidth = maxWidth - pad
        lineHeight = textAsc + textDesc
        if (lineList == null) lineList = ArrayList()
        else lineList!!.clear()
        val boundary = BreakIterator.getWordInstance()
        boundary.setText(txtInit)
        var start = boundary.first()
        var end = boundary.next()
        var prevEnd = start
        while (end != BreakIterator.DONE) {
            val line = txtInit.substring(start, end)
            val prevLine = txtInit.substring(start, prevEnd)
            val lineWidth = getTextWidth(line)
            if (lineWidth > areaWidth) {
                if (prevLine.isNotEmpty()) lineList!!.add(prevLine)
                start = prevEnd
            }
            prevEnd = end
            end = boundary.next()
        }
        val line = txtInit.substring(start, prevEnd)
        lineList!!.add(line)
        if (lines == null || lines!!.size != lineList!!.size)
            lines = Array(lineList!!.size, { "" } )
        if (lineWidths == null || lineWidths!!.size != lineList!!.size)
            lineWidths = FloatArray(lineList!!.size)
        lineList!!.toArray(lines)
        var maxLineWidth = 0f
        for (i in lines!!.indices) {
            lineWidths!![i] = getTextWidth(lines!![i])
            if (maxLineWidth < lineWidths!![i]) maxLineWidth = lineWidths!![i]
        }
        areaWidth = maxLineWidth
        val areaHeight = lineHeight * lines!!.size
        mWidth = areaWidth + pad * 2
        mHeight = areaHeight + pad * 2
    }

    override fun paint(canvas: Canvas) {
        setFontSize(mFontSize)
        setFill(true)
        setColor(backgroundColor)
        paintRoundedRect(canvas, 0f, 0f, mWidth, mHeight)
        setFill(false)
        setColor(borderColor)
        paintRoundedRect(canvas, 0f, 0f, mWidth, mHeight)
        for (i in lines!!.indices) {
            val line = lines!![i]
            setFill(true)
            setStrokeWidth(0f)
            setColor(textColor)
            paintText(canvas, pad, pad + lineHeight * i + textAsc, line)
        }
    }

    override fun getWidth(): Float = mWidth
    override fun getHeight(): Float = mHeight
}