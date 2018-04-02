package com.kkaun.tinyarbrowser.util

class ScreenPositionHelper() {

    var x = 0f
    var y = 0f

    init { set(0f, 0f) }

    operator fun set(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    fun rotate(t: Double) {
        val xp = Math.cos(t).toFloat() * x - Math.sin(t).toFloat() * y
        val yp = Math.sin(t).toFloat() * x + Math.cos(t).toFloat() * y
        x = xp
        y = yp
    }

    fun add(x: Float, y: Float) {
        this.x += x
        this.y += y
    }

    override fun toString(): String = "x=$x y=$y"
}